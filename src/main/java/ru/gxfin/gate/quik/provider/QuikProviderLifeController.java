package ru.gxfin.gate.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gxfin.gate.quik.events.*;

/**
 * Обработка событий о запуске и останове (жизненый цикл) Провайдера
 * @author Vladimir Gagarkin
 * @since 1.0
 */
@Slf4j
public class QuikProviderLifeController {
    @Autowired
    private QuikProvider provider;

    /**
     * Обработчик команды о запуске провайдера
     *
     * @param event команда о запуске провайдера
     */
    @EventListener(ProviderStartEvent.class)
    public void onEvent(ProviderStartEvent event) {
        log.info("Starting onEvent(ProviderStartEvent event)");
        this.provider.start();
        log.info("Finished onEvent(ProviderStartEvent event)");
    }

    /**
     * Обработчик команды об остановке провайдера
     *
     * @param event команда об остановке провайдера
     */
    @EventListener(ProviderStopEvent.class)
    public void onEvent(ProviderStopEvent event) {
        log.info("Starting onEvent(ProviderStopEvent event)");
        this.provider.stop();
        log.info("Finished onEvent(ProviderStopEvent event)");
    }

    /**
     * Обработчик команды о запуске провайдера с очисткой снапшота
     * @param event команда о запуске провайдера с очисткой снапшота
     */
    @SuppressWarnings("ImplicitArrayToString")
    @EventListener(ProviderStartWithCleanEvent.class)
    public void onEvent(ProviderStartWithCleanEvent event) {
        log.info("Starting onEvent(ProviderStartWithCleanEvent event)");
        try {
            this.provider.clean();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        this.provider.start();
        log.info("Finished onEvent(ProviderStartWithCleanEvent event)");
    }

    /**
     * Обработчик команды об остановке провайдера с очисткой снапшота
     * @param event команда об остановке провайдера с очисткой снапшота
     */
    @SuppressWarnings("ImplicitArrayToString")
    @EventListener(ProviderStopWithCleanEvent.class)
    public void onEvent(ProviderStopWithCleanEvent event) {
        log.info("Starting onEvent(ProviderStopWithCleanEvent event)");
        this.provider.stop();
        try {
            this.provider.clean();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        log.info("Finished onEvent(ProviderStopWithCleanEvent event)");
    }

    /**
     * Обработчик команды об очистке данных провайдера
     * @param event команда об очистке данных провайдера
     */
    @SuppressWarnings("ImplicitArrayToString")
    @EventListener(ProviderCleanEvent.class)
    public void onEvent(ProviderCleanEvent event) {
        log.info("Starting onEvent(ProviderCleanEvent event)");
        try {
            this.provider.clean();
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        log.info("Finished onEvent(ProviderCleanEvent event)");
    }
}
