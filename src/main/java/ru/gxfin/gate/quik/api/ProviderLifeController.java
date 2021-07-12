package ru.gxfin.gate.quik.api;

import ru.gxfin.gate.quik.events.*;

/**
 * Обработка событий о запуске и останове (жизненый цикл) Провайдера
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface ProviderLifeController {

    /**
     * Обработчик команды о запуске провайдера
     * @param event команда о запуске провайдера
     */
    void onEvent(ProviderStartEvent event);

    /**
     * Обработчик команды об остановке провайдера
     * @param event команда об остановке провайдера
     */
    void onEvent(ProviderStopEvent event);

    /**
     * Обработчик команды о запуске провайдера с очисткой снапшота
     * @param event команда о запуске провайдера с очисткой снапшота
     */
    void onEvent(ProviderStartWithCleanEvent event);

    /**
     * Обработчик команды об остановке провайдера с очисткой снапшота
     * @param event команда об остановке провайдера с очисткой снапшота
     */
    void onEvent(ProviderStopWithCleanEvent event);

    /**
     * Обработчик команды об очистке данных провайдера
     * @param event команда об очистке данных провайдера
     */
    void onEvent(ProviderCleanEvent event);
}
