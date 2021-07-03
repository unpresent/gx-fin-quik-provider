package ru.gxfin.quik.api;

import ru.gxfin.common.worker.Worker;
import ru.gxfin.quik.errors.ProviderException;

public interface Provider extends Worker {
    /**
     * Очистка данных
     */
    void clean() throws ProviderException;
}
