package ru.gx.fin.gate.quik.datacontrollers;

import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;

/**
 * Управление каким-то одним потоком данных:
 * Запрос пакета данных у Connector-а, контроль за необходимостью немедленного повторения чтения.
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface ProviderDataController {
    String headerKeyAllCount = "allCount";
    String headerKeyLastIndex = "lastIndex";

    void load(SimpleWorkerOnIterationExecuteEvent iterationExecuteEvent) throws Exception;

    void clean();
}
