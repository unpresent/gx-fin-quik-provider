package ru.gxfin.gate.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gxfin.common.worker.AbstractIterationExecuteEvent;
import ru.gxfin.common.worker.AbstractStartingExecuteEvent;
import ru.gxfin.common.worker.AbstractStoppingExecuteEvent;
import ru.gxfin.common.worker.AbstractWorker;
import ru.gxfin.gate.quik.datacontrollers.ProviderDataController;
import ru.gxfin.gate.quik.connector.QuikConnector;
import ru.gxfin.gate.quik.events.ProviderIterationExecuteEvent;
import ru.gxfin.gate.quik.events.ProviderSettingsChangedEvent;
import ru.gxfin.gate.quik.events.ProviderStartingExecuteEvent;
import ru.gxfin.gate.quik.events.ProviderStoppingExecuteEvent;

import java.io.IOException;
import java.util.List;

@Slf4j
public class QuikProvider extends AbstractWorker {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Autowired
    private QuikProviderSettingsController settings;

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
        return this.settings.getMinTimePerIterationMs();
    }

    @Override
    protected int getTimoutRunnerLifeMs() {
        return this.settings.getTimeoutLifeMs();
    }

    @Override
    public int getWaitOnStopMs() {
        return this.settings.getWaitOnStopMs();
    }

    @Override
    public int getWaitOnRestartMs() {
        return this.settings.getWaitOnRestartMs();
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

    @Override
    protected AbstractStartingExecuteEvent createStartingExecuteEvent() {
        return new ProviderStartingExecuteEvent(this);
    }

    @Override
    protected AbstractStoppingExecuteEvent createStoppingExecuteEvent() {
        return new ProviderStoppingExecuteEvent(this);
    }

    @EventListener(ProviderStartingExecuteEvent.class)
    public void startingExecute() {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @EventListener(ProviderStoppingExecuteEvent.class)
    public void stoppingExecute() {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @EventListener(ProviderIterationExecuteEvent.class)
    public void iterationExecute(ProviderIterationExecuteEvent event) {
        log.debug("Starting iterationExecute()");
        try {
            runnerIsLifeSet();

            if (!internalCheckConnected(event)) {
                return;
            }
            for (var dataController : this.dataControllers) {
                runnerIsLifeSet();
                dataController.load(event);
            }
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(event, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    public void clean() {
        for (var dataController : this.dataControllers) {
            runnerIsLifeSet();
            dataController.clean();
        }
    }

    protected boolean internalCheckConnected(ProviderIterationExecuteEvent event) {
        if (!this.connector.isActive()) {
            try {
                final var n = this.settings.getAttemptsOnConnect();
                log.info("Starting (" + n + ") attempts for connector.tryConnect()");
                for (var i = 0; i < n; i++) {
                    if (!isRunning()) {
                        return false;
                    }

                    runnerIsLifeSet();
                    if (this.connector.tryConnect()) {
                        log.info("Provider connected!");
                        return true;
                    }

                    Thread.sleep(this.settings.getPauseOnConnectMs());
                }
                return false;
            } catch (Exception e) {
                internalTreatmentExceptionOnDataRead(event, e);
                return false;
            } finally {
                log.info("Finished attempts for connector.tryConnect()");
            }
        }
        return true;
    }

    @SuppressWarnings("ImplicitArrayToString")
    private void internalTreatmentExceptionOnDataRead(ProviderIterationExecuteEvent event, Exception e) {
        log.error(e.getMessage());
        log.error(e.getStackTrace().toString());
        if (e instanceof InterruptedException) {
            log.info("event.setStopExecution(true)");
            event.setStopExecution(true);
        } else {
            log.info("event.setNeedRestart(true)");
            event.setNeedRestart(true);
        }
    }
}
