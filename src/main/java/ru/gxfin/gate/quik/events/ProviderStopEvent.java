package ru.gxfin.gate.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости остановить провайдер
 * @since 1.0
 */
@ToString
@EqualsAndHashCode
public class ProviderStopEvent extends ApplicationEvent {
    public ProviderStopEvent(Object source) {
        super(source);
    }
}