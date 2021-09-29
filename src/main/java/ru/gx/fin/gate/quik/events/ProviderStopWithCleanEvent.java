package ru.gx.fin.gate.quik.events;

import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости остановить провайдер с очисткой состояния
 * @since 1.0
 */
public class ProviderStopWithCleanEvent extends ApplicationEvent {
    public ProviderStopWithCleanEvent(Object source) {
        super(source);
    }
}
