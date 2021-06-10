package ru.gagarkin.gxfin.quik.events;

import org.springframework.context.ApplicationEvent;

/**
 * Событие-команада о необходимости прочтитать пакет дынных
 * @since 1.0
 */
public class AbstractProviderDataEvent extends ApplicationEvent  {
    public AbstractProviderDataEvent(Object source) {
        super(source);
    }
}
