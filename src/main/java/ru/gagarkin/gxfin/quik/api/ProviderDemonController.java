package ru.gagarkin.gxfin.quik.api;

/**
 * Контролер фонового режима. Следит за Runner-ом:
 * при взникновении Timeout-ов или при поднятом флаге необходимости перезапуска отдает команду перезапуска Провайдеру
 * @author Vladimir Gagarkin
 * @since 1.0
 */
public interface ProviderDemonController extends Runnable {
    /**
     * Признак того, что данный контролер запущен
     */
    boolean isActive();
}
