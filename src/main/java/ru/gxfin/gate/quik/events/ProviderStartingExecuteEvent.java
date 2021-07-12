package ru.gxfin.gate.quik.events;

import ru.gxfin.common.worker.AbstractStartingExecuteEvent;

public class ProviderStartingExecuteEvent extends AbstractStartingExecuteEvent {
    public ProviderStartingExecuteEvent(Object source) {
        super(source);
    }
}
