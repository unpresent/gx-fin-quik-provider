package ru.gxfin.gate.quik.events;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости остановить провайдер с очисткой состояния
 * @since 1.0
 */
@ToString
@EqualsAndHashCode
public class ProviderStopWithCleanEvent extends ApplicationEvent {
    public ProviderStopWithCleanEvent(Object source) {
        super(source);
    }
}
