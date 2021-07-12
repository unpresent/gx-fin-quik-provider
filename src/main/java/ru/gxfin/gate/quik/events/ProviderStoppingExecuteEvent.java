package ru.gxfin.gate.quik.events;

import ru.gxfin.common.worker.AbstractStoppingExecuteEvent;

public class ProviderStoppingExecuteEvent extends AbstractStoppingExecuteEvent {
    public ProviderStoppingExecuteEvent(Object source) {
        super(source);
    }
}
