package ru.gx.fin.gate.quik.events;

import org.springframework.context.ApplicationEvent;

public class QuikProviderDoCleanEvent extends ApplicationEvent {
    public QuikProviderDoCleanEvent(Object source) {
        super(source);
    }
}
