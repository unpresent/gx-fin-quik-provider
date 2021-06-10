package ru.gagarkin.gxfin.quik.api;

import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.AbstractProviderDataEvent;

import java.io.IOException;

/**
 * Управление каким-то одним потоком данных:
 * Запрос пакета данных у Connector-а, контроль за необходимостью немедленного повторения чения
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface ProviderDataController<E extends AbstractProviderDataEvent> {
    /**
     * Обработчик команды о чтении
     * @param event команда о чтении
     */
    void onEvent(E event) throws ProviderException, IOException, QuikConnectorException;

    boolean needReload();

    AbstractProviderDataEvent createEvent(Object source);
}
