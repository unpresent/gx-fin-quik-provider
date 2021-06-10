package ru.gagarkin.gxfin.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderSettingsChangedEvent;

/**
 * Провайдер. Основное назначение:
 * Запуск Runner-а, DemonController-а
 * @author Vladimir Gagarkin
 * @since 1.0
 */
@Slf4j
public class QuikProvider implements Provider {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Settings">
    /**
     * Корень имени NamedPipe-а
     */
    private String quikPipeName;

    /**
     * Размера буферов (одинаковый) для входящих и исходящих сообщений
     */
    private int bufferSize;

    /**
     * Установка всех Setting-ов из контроллера Setting-ов
     */
    private void resetSettings() {
        this.quikPipeName = this.settings.getQuikPipeName();
        this.bufferSize = this.settings.getBufferSize();
    }

    /**
     * Обработка события об измении установки в контролере Setting-ов
     * @param event само событие, содержит название Setting-а
     */
    @EventListener
    public void onEventChangedSettings(ProviderSettingsChangedEvent event) {
        switch (event.getSettingName()) {
            case QuikProviderSettings.QUIK_PIPE_NAME:
                this.quikPipeName = this.settings.getQuikPipeName();
                break;
            case QuikProviderSettings.BUFFER_SIZE:
                this.bufferSize = this.settings.getBufferSize();
                break;
            case QuikProviderSettings.ALL:
                resetSettings();
                break;
            default:
                break;
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields & Properties">
    private final ApplicationContext context;

    /**
     * Контролер управления настройками
     */
    private final QuikProviderSettings settings;

    /**
     * Runner - бесконечный цикл в потоке, в котором оркестрируются команды к DataController-ам
     */
    private final QuikProviderRunner runner;

    /**
     * Последний момент времени начала работы какого-либо чтения.
     * Каждый DataController передначалом чтения из NamedPipe-а должен зарегистрировать факт старта.
     * Требуется для целей контроля таймаута (если pipe не будет отвечать за отведенное время, то Demon снимет runner-а)
     * Требуется для целей контроля таймаута (если pipe не будет отвечать за отведенное время, то Demon снимет runner-а)
     */
    private volatile long lastExecutionStarted = -1;

    @Override
    public QuikConnector getConnector()  {
        return this.runner.getConnector();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    @Autowired
    public QuikProvider(ApplicationContext context, QuikProviderSettings settings) {
        this.context = context;
        this.settings = settings;
        resetSettings();
        var connector = new QuikConnector(this.quikPipeName, this.bufferSize);
        this.runner = new QuikProviderRunner(this.context, connector, this.settings);
    }

    @Override
    public void registerDataController(ProviderDataController controller) {
        this.runner.registerDataController(controller);
    }

    @Override
    public void unRegisterDataController(ProviderDataController controller) {
        this.runner.unRegisterDataController(controller);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">
    @Override
    public void start() {
        log.info("Starting start()");
        new Thread(this.runner).start();
        // runner.run();
        log.info("Finished start()");
    }

    @Override
    public void stop() {
        log.info("Starting stop()");
        this.runner.stop();
        try {
            Thread.sleep(100);
            var worker = this.runner.getWorker();
            if (worker != null) {
                log.info("runner.getWorker().interrupt()!");
                worker.interrupt();
            };
            var connector = this.runner.getConnector();
            if (connector != null && connector.isActive()) {
                log.info("runner.getConnector().disconnect()!");
                connector.disconnect();
            };
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        log.info("Finished stop()");
    }

    @Override
    public void clean() throws ProviderException {
        if (runner.isRunning()) {
            log.warn("Недопустимо событие Clean при работающем Provider-е!");
            throw new ProviderException("Недопустимо событие Clean при работающем Provider-е!");
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">

    @Override
    public void registerStartExecution() {
        this.lastExecutionStarted = System.currentTimeMillis();
    }

    @Override
    public void registerFinishExecution() throws ProviderException {
        if (this.lastExecutionStarted > 0) {
            this.lastExecutionStarted = -1;
        } else {
            log.warn("Недопустимо выполнение registerFinishExecution() при отсутствии регистрации выполнения");
            throw new ProviderException("Недопустимо выполнение registerFinishExecution() при отсутствии регистрации выполнения!");
        }
    }

    public long getPassedSinceLastStart() {
        var now = System.currentTimeMillis();
        return this.lastExecutionStarted > 0 ? now - this.lastExecutionStarted : -1;
    }

    @Override
    public boolean needRestart() {
        return this.runner.isNeedRestart();
    }

    @Override
    public void close() {
        stop();
    }

}
