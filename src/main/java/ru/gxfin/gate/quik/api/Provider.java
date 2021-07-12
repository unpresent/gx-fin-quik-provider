package ru.gxfin.gate.quik.api;

import ru.gxfin.common.worker.Worker;
import ru.gxfin.gate.quik.errors.ProviderException;

public interface Provider extends Worker {
    /**
     * Очистка данных
     */
    void clean() throws ProviderException;
}
