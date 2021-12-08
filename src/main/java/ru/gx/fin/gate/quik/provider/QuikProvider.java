package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gx.core.simpleworker.SimpleWorker;
import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;
import ru.gx.core.simpleworker.SimpleWorkerOnStartingExecuteEvent;
import ru.gx.core.simpleworker.SimpleWorkerOnStoppingExecuteEvent;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.datacontrollers.ProviderDataController;
import ru.gx.fin.gate.quik.datacontrollers.QuikProviderSecuritiesDataController;

import java.io.IOException;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProvider {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private SimpleWorker simpleWorker;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikConnector connector;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private List<ProviderDataController> dataControllers;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSecuritiesDataController quikProviderSecuritiesDataController;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    @EventListener(SimpleWorkerOnStartingExecuteEvent.class)
    public void startingExecute(SimpleWorkerOnStartingExecuteEvent __) {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @EventListener(SimpleWorkerOnStoppingExecuteEvent.class)
    public void stoppingExecute(SimpleWorkerOnStoppingExecuteEvent __) {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @EventListener(SimpleWorkerOnIterationExecuteEvent.class)
    public void iterationExecute(SimpleWorkerOnIterationExecuteEvent event) {
        log.debug("Starting iterationExecute()");
        try {
            this.simpleWorker.runnerIsLifeSet();

            if (!internalCheckConnected(event)) {
                return;
            }

            for (var dataController : this.dataControllers) {
                this.simpleWorker.runnerIsLifeSet();
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
            this.simpleWorker.runnerIsLifeSet();
            dataController.clean();
        }
    }

    protected boolean internalCheckConnected(SimpleWorkerOnIterationExecuteEvent event) {
        if (!this.connector.isActive()) {
            try {
                final var n = this.settings.getAttemptsOnConnect();
                log.info("Starting (" + n + ") attempts for connector.tryConnect()");
                for (var i = 0; i < n; i++) {
                    if (!this.simpleWorker.isRunning()) {
                        return false;
                    }

                    this.simpleWorker.runnerIsLifeSet();
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

    private void internalTreatmentExceptionOnDataRead(SimpleWorkerOnIterationExecuteEvent event, Exception e) {
        log.error("", e);
        if (e instanceof InterruptedException) {
            log.info("event.setStopExecution(true)");
            event.setStopExecution(true);
        } else {
            log.info("event.setNeedRestart(true)");
            event.setNeedRestart(true);
        }
    }
}
