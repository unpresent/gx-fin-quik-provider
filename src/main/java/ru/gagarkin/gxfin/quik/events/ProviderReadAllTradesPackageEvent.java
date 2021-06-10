package ru.gagarkin.gxfin.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Событие-команада о необходимости прочтитать пакет Анонимных сделок
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderReadAllTradesPackageEvent extends AbstractProviderDataEvent {
    public ProviderReadAllTradesPackageEvent(Object source) {
        super(source);
    }
}
