package ru.gxfin.gate.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости очистить данные провайдера
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderCleanEvent extends ApplicationEvent {
    public ProviderCleanEvent(Object source) {
        super(source);
    }
}
