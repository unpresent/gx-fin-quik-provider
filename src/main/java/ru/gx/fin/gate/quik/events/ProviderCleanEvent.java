package ru.gx.fin.gate.quik.events;

import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости очистить данные провайдера
 * @since 1.0
 */
public class ProviderCleanEvent extends ApplicationEvent {
    public ProviderCleanEvent(Object source) {
        super(source);
    }
}
