package ru.gxfin.gate.quik.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости перезапустить провайдер с очисткой состояния
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderStartWithCleanEvent extends ApplicationEvent {

    /**
     * Флаг для того, чтобы стартануть, например, в выходной или ночью.
     */
    @Getter
    private final boolean forceStart;

    public ProviderStartWithCleanEvent(Object source) {
        super(source);
        forceStart = false;
    }

    public ProviderStartWithCleanEvent(Object source, boolean forceStart) {
        super(source);
        this.forceStart = forceStart;
    }
}
