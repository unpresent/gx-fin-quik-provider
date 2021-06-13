package ru.gagarkin.gxfin.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gagarkin.gxfin.common.worker.AbstractIterationExecuteEvent;
import ru.gagarkin.gxfin.common.worker.AbstractWorker;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderIterationExecuteEvent;
import ru.gagarkin.gxfin.quik.events.ProviderSettingsChangedEvent;

import java.util.List;

@Slf4j
public class QuikProvider extends AbstractWorker implements Provider {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Autowired
    private QuikProviderSettings settings;

    @Autowired
    private QuikConnector connector;

    @Autowired
    private List<ProviderDataController> dataControllers;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Settings">
    @EventListener
    public void onEventChangedSettings(ProviderSettingsChangedEvent event) {
        log.info("onEventChangedSettings({})", event.getSettingName());
    }

    @Override
    protected int getMinTimePerIterationMs() {
        return this.settings.getMinTimeMsPerIteration();
    }

    @Override
    protected int getTimoutRunnerLifeMs() {
        return this.settings.getTimeoutDataRead();
    }

    @Override
    public int getWaitOnStopMS() {
        return this.settings.getWaitMsOnStop();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    public QuikProvider() {
        super(QuikProvider.class.getSimpleName());
    }

    @Override
    protected AbstractIterationExecuteEvent createIterationExecuteEvent() {
        return new ProviderIterationExecuteEvent(this);
    }

    @EventListener(ProviderIterationExecuteEvent.class)
    public void iterationExecute(ProviderIterationExecuteEvent event) {
        try {
            if (!internalCheckConnected(event)) {
                event.setImmediateRunNextIteration(true);
                return;
            }

            for (var dataController : this.dataControllers) {
                runnerIsLifeSet();
                dataController.load(event);
            }

            if (!event.isImmediateRunNextIteration()) {
                runnerIsLifeSet();
                Thread.sleep(settings.getMinTimeMsPerIteration());
            }
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(event, e);
        }
    }

    @Override
    public void clean() throws ProviderException {
        for (var dataController : this.dataControllers) {
            runnerIsLifeSet();
            dataController.clean();
        }
    }

    protected boolean internalCheckConnected(ProviderIterationExecuteEvent event) {
        if (!this.connector.isActive()) {
            try {
                var n = this.settings.getAttemptsOnConnect();
                for (var i = 0; i < n; i++) {
                    if (!isRunning()) {
                        return false;
                    }

                    if (this.connector.tryConnect()) {
                        log.info("Provider connected!");
                        return true;
                    }

                    Thread.sleep(this.settings.getPauseMsOnConnect());
                }
                return false;
            } catch (Exception e) {
                internalTreatmentExceptionOnDataRead(event, e);
                return false;
            }
        }
        return true;
    }

    private void internalTreatmentExceptionOnDataRead(ProviderIterationExecuteEvent event, Exception e) {
        log.error(e.getMessage());
        log.error(e.getStackTrace().toString());
        if (e instanceof InterruptedException) {
            event.setStopExecution(true);
        } else {
            event.setNeedRestart(true);
        }
    }
}
