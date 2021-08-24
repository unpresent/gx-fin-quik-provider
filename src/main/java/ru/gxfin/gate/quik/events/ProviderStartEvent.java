package ru.gxfin.gate.quik.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие-сигнал о необходимости перезапустить провайдер
 * @since 1.0
 */
public class ProviderStartEvent extends ApplicationEvent {

    /**
     * Флаг для того, чтобы запустить, например, в выходной или ночью.
     */
    @Getter
    private final boolean forceStart;

    public ProviderStartEvent(Object source) {
        super(source);
        forceStart = false;
    }
}