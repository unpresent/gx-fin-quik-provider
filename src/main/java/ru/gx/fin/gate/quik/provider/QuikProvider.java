package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.gx.core.simpleworker.SimpleWorker;
import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;
import ru.gx.core.simpleworker.SimpleWorkerOnStartingExecuteEvent;
import ru.gx.core.simpleworker.SimpleWorkerOnStoppingExecuteEvent;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.datacontrollers.ProviderDataController;
import ru.gx.fin.gate.quik.datacontrollers.QuikProviderSecuritiesDataController;
import ru.gx.fin.gate.quik.datacontrollers.QuikProviderSessionStateDataController;
import ru.gx.fin.gate.quik.events.QuikProviderDoCleanEvent;

import java.io.IOException;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
@RequiredArgsConstructor
@Component
public class QuikProvider {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Getter(PROTECTED)
    @NotNull
    private final QuikProviderSettingsContainer settings;

    @Getter(PROTECTED)
    @NotNull
    private final SimpleWorker simpleWorker;

    @Getter(PROTECTED)
    @NotNull
    private final QuikConnector connector;

    @Getter(PROTECTED)
    @NotNull
    private final List<ProviderDataController> dataControllers;

    @Getter(PROTECTED)
    @NotNull
    private final QuikProviderSessionStateDataController quikProviderSessionStateDataController;

    @Getter(PROTECTED)
    @NotNull
    private final QuikProviderSecuritiesDataController quikProviderSecuritiesDataController;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    @SuppressWarnings("unused")
    @EventListener(SimpleWorkerOnStartingExecuteEvent.class)
    public void startingExecute(SimpleWorkerOnStartingExecuteEvent event) {
        try {
            this.connector.disconnect();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    @SuppressWarnings("unused")
    @EventListener(SimpleWorkerOnStoppingExecuteEvent.class)
    public void stoppingExecute(SimpleWorkerOnStoppingExecuteEvent event) {
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

            // Сначала загружаем State
            this.quikProviderSessionStateDataController.load(event);
            if (event.isNeedRestart() || event.isImmediateRunNextIteration() || event.isStopExecution()) {
                return;
            }

            // Потом загружаем ЦБ
            this.quikProviderSecuritiesDataController.load(event);
            if (event.isNeedRestart() || event.isImmediateRunNextIteration() || event.isStopExecution()) {
                return;
            }

            // Потом остальные
            for (var dataController : this.dataControllers) {
                if (dataController == this.quikProviderSessionStateDataController
                        || dataController == this.quikProviderSecuritiesDataController) {
                    continue;
                }

                this.simpleWorker.runnerIsLifeSet();
                dataController.load(event);
                if (event.isNeedRestart() || event.isImmediateRunNextIteration() || event.isStopExecution()) {
                    return;
                }
            }
        } catch (Exception e) {
            internalTreatmentExceptionOnDataRead(event, e);
        } finally {
            log.debug("Finished iterationExecute()");
        }
    }

    @SuppressWarnings("unused")
    @EventListener(QuikProviderDoCleanEvent.class)
    public void doClean(QuikProviderDoCleanEvent event) {
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
