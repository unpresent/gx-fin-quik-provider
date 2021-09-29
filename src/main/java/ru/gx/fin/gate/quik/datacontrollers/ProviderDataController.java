package ru.gx.fin.gate.quik.datacontrollers;

import ru.gx.fin.gate.quik.errors.ProviderException;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.worker.SimpleIterationExecuteEvent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Управление каким-то одним потоком данных:
 * Запрос пакета данных у Connector-а, контроль за необходимостью немедленного повторения чтения.
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface ProviderDataController {
    void load(SimpleIterationExecuteEvent iterationExecuteEvent) throws ProviderException, IOException, QuikConnectorException, ExecutionException, InterruptedException;

    void clean();
}
