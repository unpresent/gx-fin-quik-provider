package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.gx.core.settings.StandardSettingsController;
import ru.gx.core.settings.UnknownApplicationSettingException;
import ru.gx.fin.gate.quik.config.ConfigurationPropertiesServiceKafka;
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

        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_ALL_TRADES, ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_ALL_TRADES_DEFAULT_VALUE);
        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_DEALS, ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_DEALS_DEFAULT_VALUE);
        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_ORDERS, ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_ORDERS_DEFAULT_VALUE);
        this.standardSettingsController.loadStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_SECURITIES, ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_SECURITIES_DEFAULT_VALUE);
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
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_ALL_TRADES);
    }

    public String getOutcomeTopicDeals() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_DEALS);
    }

    public String getOutcomeTopicOrders() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_ORDERS);
    }

    public String getOutcomeTopicSecurities() {
        return this.standardSettingsController.getStringSetting(ConfigurationPropertiesServiceKafka.OUTCOME_TOPIC_SECURITIES);
    }
}