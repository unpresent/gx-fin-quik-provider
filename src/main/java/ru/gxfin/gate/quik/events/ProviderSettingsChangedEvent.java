package ru.gxfin.gate.quik.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Событие об изменении параметра системы.
 * @since 1.0
 */
public class ProviderSettingsChangedEvent extends ApplicationEvent {
    /**
     * Имя параметра, который изменился
     */
    @Getter
    private final String settingName;

    public ProviderSettingsChangedEvent(Object source, String settingName) {
        super(source);
        this.settingName = settingName;
    }
}
