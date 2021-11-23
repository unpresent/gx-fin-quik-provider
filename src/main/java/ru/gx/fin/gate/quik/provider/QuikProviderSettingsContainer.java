package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.config.ConfigurationPropertiesKafka;
import ru.gx.fin.gate.quik.config.ConfigurationPropertiesQuik;
import ru.gx.settings.StandardSettingsController;
import ru.gx.settings.UnknownApplicationSettingException;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProviderSettingsContainer {
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private StandardSettingsController standardSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.standardSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.ATTEMPTS_ON_CONNECT);
        this.standardSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.PAUSE_ON_CONNECT_MS);
        this.standardSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.INTERVAL_MANDATORY_READ_STATE_MS);

        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ALL_TRADES);
        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_DEALS);
        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ORDERS);
        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_SECURITIES);
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

    public String getOutcomeTopicAllTrades() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ALL_TRADES);
    }

    public String getOutcomeTopicDeals() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_DEALS);
    }

    public String getOutcomeTopicOrders() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ORDERS);
    }

    public String getOutcomeTopicSecurities() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_SECURITIES);
    }
}