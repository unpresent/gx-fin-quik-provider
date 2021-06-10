package ru.gagarkin.gxfin.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Событие-команада о необходимости прочтитать пакет Сделок
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderReadDealsPackageEvent extends AbstractProviderDataEvent {
    public ProviderReadDealsPackageEvent(Object source) {
        super(source);
    }
}

