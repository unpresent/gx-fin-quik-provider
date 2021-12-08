package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.settings.StandardSettingsController;
import ru.gx.core.settings.UnknownApplicationSettingException;
import ru.gx.fin.gate.quik.config.ConfigurationPropertiesQuik;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProviderSettingsContainer {
    @Getter(PROTECTED)
    @NotNull
    private final StandardSettingsController standardSettingsController;

    public QuikProviderSettingsContainer(@NotNull final StandardSettingsController standardSettingsController) {
        super();
        this.standardSettingsController = standardSettingsController;
    }

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.standardSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.ATTEMPTS_ON_CONNECT);
        this.standardSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.PAUSE_ON_CONNECT_MS);
        this.standardSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.INTERVAL_MANDATORY_READ_STATE_MS);
    }

    public int getAttemptsOnConnect() {
        return this.standardSettingsController.getIntegerSetting(ConfigurationPropertiesQuik.ATTEMPTS_ON_CONNECT);
    }

    public int getPauseOnConnectMs() {
        return this.standardSettingsController.getIntegerSetting(ConfigurationPropertiesQuik.PAUSE_ON_CONNECT_MS);
    }

    public int getIntervalMandatoryReadStateMs() {
        return this.standardSettingsController.getIntegerSetting(ConfigurationPropertiesQuik.INTERVAL_MANDATORY_READ_STATE_MS);
    }
}