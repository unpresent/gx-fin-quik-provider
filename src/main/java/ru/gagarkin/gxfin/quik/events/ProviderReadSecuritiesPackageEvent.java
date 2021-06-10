package ru.gagarkin.gxfin.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Событие-команада о необходимости прочтитать пакет ФИ
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderReadSecuritiesPackageEvent extends AbstractProviderDataEvent {
    public ProviderReadSecuritiesPackageEvent(Object source) {
        super(source);
    }
}