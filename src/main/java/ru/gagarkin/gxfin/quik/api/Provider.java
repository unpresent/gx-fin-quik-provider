package ru.gagarkin.gxfin.quik.api;

import ru.gagarkin.gxfin.common.worker.Worker;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

public interface Provider extends Worker {
    /**
     * Очистка данных
     */
    void clean() throws ProviderException;
}
