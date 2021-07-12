package ru.gxfin.gate.quik.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости перезапустить провайдер
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderStartEvent extends ApplicationEvent {

    /**
     * Флаг для того, чтобы стартануть, например, в выходной или ночью.
     */
    @Getter
    private final boolean forceStart;

    public ProviderStartEvent(Object source) {
        super(source);
        forceStart = false;
    }

    public ProviderStartEvent(Object source, boolean forceStart) {
        super(source);
        this.forceStart = forceStart;
    }

}