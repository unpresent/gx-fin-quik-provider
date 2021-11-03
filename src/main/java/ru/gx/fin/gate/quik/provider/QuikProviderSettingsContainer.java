package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.config.ConfigurationPropertiesKafka;
import ru.gx.fin.gate.quik.config.ConfigurationPropertiesQuik;
import ru.gx.settings.SimpleSettingsController;
import ru.gx.settings.UnknownApplicationSettingException;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProviderSettingsContainer {
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private SimpleSettingsController simpleSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.simpleSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.ATTEMPTS_ON_CONNECT);
        this.simpleSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.PAUSE_ON_CONNECT_MS);
        this.simpleSettingsController.loadIntegerSetting(ConfigurationPropertiesQuik.INTERVAL_MANDATORY_READ_STATE_MS);

        this.simpleSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ALL_TRADES);
        this.simpleSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_DEALS);
        this.simpleSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ORDERS);
        this.simpleSettingsController.loadStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_SECURITIES);
    }

    public int getAttemptsOnConnect() {
        return this.simpleSettingsController.getIntegerSetting(ConfigurationPropertiesQuik.ATTEMPTS_ON_CONNECT);
    }

    public int getPauseOnConnectMs() {
        return this.simpleSettingsController.getIntegerSetting(ConfigurationPropertiesQuik.PAUSE_ON_CONNECT_MS);
    }

    public int getIntervalMandatoryReadStateMs() {
        return this.simpleSettingsController.getIntegerSetting(ConfigurationPropertiesQuik.INTERVAL_MANDATORY_READ_STATE_MS);
    }

    public String getOutcomeTopicAllTrades() {
        return this.simpleSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ALL_TRADES);
    }

    public String getOutcomeTopicDeals() {
        return this.simpleSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_DEALS);
    }

    public String getOutcomeTopicOrders() {
        return this.simpleSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_ORDERS);
    }

    public String getOutcomeTopicSecurities() {
        return this.simpleSettingsController.getStringSetting(ConfigurationPropertiesKafka.OUTCOME_TOPIC_SECURITIES);
    }
}