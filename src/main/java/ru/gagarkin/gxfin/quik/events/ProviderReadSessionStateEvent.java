package ru.gagarkin.gxfin.quik.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.gagarkin.gxfin.gate.quik.dto.SessionState;

/**
 * Событие-команада о необходимости прочтитать Состояние сессии Quik-а
 * @since 1.0
 */
@ToString
@EqualsAndHashCode(callSuper = false)
public class ProviderReadSessionStateEvent extends AbstractProviderDataEvent {
    @Getter
    @Setter
    private SessionState lastSessionState;

    @Getter
    @Setter
    private long lastReadedSessionStateMs;

    public ProviderReadSessionStateEvent(Object source) {
        super(source);
    }
}