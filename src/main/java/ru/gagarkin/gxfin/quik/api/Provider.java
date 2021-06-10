package ru.gagarkin.gxfin.quik.api;

import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.Closeable;

/**
 * Провайдер. Основное назначение:
 * Запуск Runner-а, DemonController-а
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface Provider extends Closeable {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main API">
    void start();

    void stop();

    void clean() throws ProviderException;

    boolean needRestart();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Registration & deregistration data controllers">
    void registerDataController(ProviderDataController controller);

    void unRegisterDataController(ProviderDataController controller);
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Control of executions timeout">
    void registerStartExecution();

    void registerFinishExecution() throws ProviderException;

    long getPassedSinceLastStart();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Additional">
    QuikConnector getConnector();
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
