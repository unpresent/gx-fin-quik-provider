package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.settings.SimpleSettingsController;
import ru.gx.settings.UnknownApplicationSettingException;

import javax.annotation.PostConstruct;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProviderSettingsContainer {
    String QUIK_PIPE_NAME = "quik.pipe_name";
    String BUFFER_SIZE = "quik.buffer_size";
    String ATTEMPTS_ON_CONNECT = "quik.attempts_on_connect";
    String PAUSE_ON_CONNECT_MS = "quik.pause_on_connect_ms";
    String INTERVAL_MANDATORY_READ_STATE_MS = "quik.interval_mandatory_read_state_ms";

    String OUTCOME_TOPIC_ALL_TRADES = "kafka.outcome_topic.all_trades";
    String OUTCOME_TOPIC_DEALS = "kafka.outcome_topic.deals";
    String OUTCOME_TOPIC_ORDERS = "kafka.outcome_topic.orders";
    String OUTCOME_TOPIC_SECURITIES = "kafka.outcome_topic.securities";

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    @NotNull
    private SimpleSettingsController simpleSettingsController;

    @PostConstruct
    public void init() throws UnknownApplicationSettingException {
        this.simpleSettingsController.loadStringSetting(QUIK_PIPE_NAME);
        this.simpleSettingsController.loadIntegerSetting(BUFFER_SIZE);
        this.simpleSettingsController.loadIntegerSetting(ATTEMPTS_ON_CONNECT);
        this.simpleSettingsController.loadIntegerSetting(PAUSE_ON_CONNECT_MS);
        this.simpleSettingsController.loadIntegerSetting(INTERVAL_MANDATORY_READ_STATE_MS);

        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_ALL_TRADES);
        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_DEALS);
        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_ORDERS);
        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_SECURITIES);
    }

    public String getQuikPipeName() {
        return this.simpleSettingsController.getStringSetting(QUIK_PIPE_NAME);
    }

    public int getBufferSize() {
        return this.simpleSettingsController.getIntegerSetting(BUFFER_SIZE);
    }

    public int getAttemptsOnConnect() {
        return this.simpleSettingsController.getIntegerSetting(ATTEMPTS_ON_CONNECT);
    }

    public int getPauseOnConnectMs() {
        return this.simpleSettingsController.getIntegerSetting(PAUSE_ON_CONNECT_MS);
    }

    public int getIntervalMandatoryReadStateMs() {
        return this.simpleSettingsController.getIntegerSetting(INTERVAL_MANDATORY_READ_STATE_MS);
    }

    public String getOutcomeTopicAllTrades() {
        return this.simpleSettingsController.getStringSetting(OUTCOME_TOPIC_ALL_TRADES);
    }

    public String getOutcomeTopicDeals() {
        return this.simpleSettingsController.getStringSetting(OUTCOME_TOPIC_DEALS);
    }

    public String getOutcomeTopicOrders() {
        return this.simpleSettingsController.getStringSetting(OUTCOME_TOPIC_ORDERS);
    }

    public String getOutcomeTopicSecurities() {
        return this.simpleSettingsController.getStringSetting(OUTCOME_TOPIC_SECURITIES);
    }
}
