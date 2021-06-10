package ru.gagarkin.gxfin.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Событие-команада о необходимости прочтитать пакет Поручений
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderReadOrdersPackageEvent extends AbstractProviderDataEvent {
    public ProviderReadOrdersPackageEvent(Object source) {
        super(source);
    }
}