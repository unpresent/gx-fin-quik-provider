package ru.gxfin.gate.quik.events;

import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости остановить провайдер
 * @since 1.0
 */
public class ProviderStopEvent extends ApplicationEvent {
    public ProviderStopEvent(Object source) {
        super(source);
    }
}