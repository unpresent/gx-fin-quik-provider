package ru.gxfin.gate.quik.datacontrollers;

import ru.gxfin.gate.quik.errors.ProviderException;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.gate.quik.events.ProviderIterationExecuteEvent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Управление каким-то одним потоком данных:
 * Запрос пакета данных у Connector-а, контроль за необходимостью немедленного повторения чения
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface ProviderDataController {
    void load(ProviderIterationExecuteEvent iterationExecuteEvent) throws ProviderException, IOException, QuikConnectorException, ExecutionException, InterruptedException;

    void clean();
}
