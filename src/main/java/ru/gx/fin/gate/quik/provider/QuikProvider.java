package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.datacontrollers.ProviderDataController;
import ru.gx.worker.SimpleIterationExecuteEvent;
import ru.gx.worker.SimpleStartingExecuteEvent;
import ru.gx.worker.SimpleStoppingExecuteEvent;
import ru.gx.worker.SimpleWorker;

import java.io.IOException;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProvider {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsController settings;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private SimpleWorker simpleWorker;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikConnector connector;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private List<ProviderDataController> dataControllers;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

    public QuikProvider() {
    }


    @EventListener(SimpleStartingExecuteEvent.class)
    public void startingExecute(SimpleStartingExecuteEvent __) {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @EventListener(SimpleStoppingExecuteEvent.class)
    public void stoppingExecute(SimpleStoppingExecuteEvent __) {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @EventListener(SimpleIterationExecuteEvent.class)
    public void iterationExecute(SimpleIterationExecuteEvent event) {
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

    protected boolean internalCheckConnected(SimpleIterationExecuteEvent event) {
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

    @SuppressWarnings("ImplicitArrayToString")
    private void internalTreatmentExceptionOnDataRead(SimpleIterationExecuteEvent event, Exception e) {
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
