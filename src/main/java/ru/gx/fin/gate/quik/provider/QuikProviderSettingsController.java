package ru.gx.fin.gate.quik.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.settings.SettingsController;
import ru.gx.settings.SimpleSettingsController;
import ru.gx.settings.UnknownApplicationSettingException;

import static lombok.AccessLevel.PROTECTED;

@Slf4j
public class QuikProviderSettingsController implements SettingsController {
    String QUIK_PIPE_NAME = "quik.pipe_name";
    String BUFFER_SIZE = "quik.buffer_size";
    String ATTEMPTS_ON_CONNECT = "quik.attempts_on_connect";
    String PAUSE_ON_CONNECT_MS = "quik.pause_on_connect_ms";

    String WAIT_ON_STOP_MS = "quik-provider.wait_on_stop_ms";
    String WAIT_ON_RESTART_MS = "quik-provider.wait_on_restarts_ms";
    String MIN_TIME_PER_ITERATION_MS = "quik-provider.min_time_per_iteration_ms";
    String INTERVAL_MANDATORY_READ_STATE_MS = "quik-provider.interval_mandatory_read_state_ms";
    String TIMEOUT_LIFE_MS = "quik-provider.timeout_life_ms";

    String OUTCOME_TOPIC_ALL_TRADES = "kafka.outcome_topic.all_trades";
    String OUTCOME_TOPIC_DEALS = "kafka.outcome_topic.deals";
    String OUTCOME_TOPIC_ORDERS = "kafka.outcome_topic.orders";
    String OUTCOME_TOPIC_SECURITIES = "kafka.outcome_topic.securities";

    @Getter(PROTECTED)
    @NotNull
    private final SimpleSettingsController simpleSettingsController;

    @Autowired
    public QuikProviderSettingsController(@NotNull final SimpleSettingsController simpleSettingsController) throws UnknownApplicationSettingException {
        this.simpleSettingsController = simpleSettingsController;

        this.simpleSettingsController.loadStringSetting(QUIK_PIPE_NAME);
        this.simpleSettingsController.loadIntegerSetting(BUFFER_SIZE);
        this.simpleSettingsController.loadIntegerSetting(ATTEMPTS_ON_CONNECT);
        this.simpleSettingsController.loadIntegerSetting(PAUSE_ON_CONNECT_MS);

        this.simpleSettingsController.loadIntegerSetting(WAIT_ON_STOP_MS);
        this.simpleSettingsController.loadIntegerSetting(WAIT_ON_RESTART_MS);
        this.simpleSettingsController.loadIntegerSetting(MIN_TIME_PER_ITERATION_MS);
        this.simpleSettingsController.loadIntegerSetting(INTERVAL_MANDATORY_READ_STATE_MS);
        this.simpleSettingsController.loadIntegerSetting(TIMEOUT_LIFE_MS);

        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_ALL_TRADES);
        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_DEALS);
        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_ORDERS);
        this.simpleSettingsController.loadStringSetting(OUTCOME_TOPIC_SECURITIES);
    }

    @Override
    @Nullable
    public Object getSetting(@NotNull final String s) {
        return this.simpleSettingsController.getSetting(s);
    }

    @Override
    @NotNull
    public Integer getIntegerSetting(@NotNull final String s) throws ClassCastException {
        return this.simpleSettingsController.getIntegerSetting(s);
    }

    @Override
    @NotNull
    public String getStringSetting(@NotNull final String s) throws ClassCastException {
        return this.simpleSettingsController.getStringSetting(s);
    }

    @Override
    public void setSetting(@NotNull final String s, @Nullable final Object o) {
        this.simpleSettingsController.setSetting(s, o);
    }

    public String getQuikPipeName() {
        return getStringSetting(QUIK_PIPE_NAME);
    }

    public int getBufferSize() {
        return getIntegerSetting(BUFFER_SIZE);
    }

    public int getAttemptsOnConnect() {
        return getIntegerSetting(ATTEMPTS_ON_CONNECT);
    }

    public int getPauseOnConnectMs() {
        return getIntegerSetting(PAUSE_ON_CONNECT_MS);
    }

    public int getIntervalMandatoryReadStateMs() {
        return getIntegerSetting(INTERVAL_MANDATORY_READ_STATE_MS);
    }

    public String getOutcomeTopicAllTrades() {
        return getStringSetting(OUTCOME_TOPIC_ALL_TRADES);
    }

    public String getOutcomeTopicDeals() {
        return getStringSetting(OUTCOME_TOPIC_DEALS);
    }

    public String getOutcomeTopicOrders() {
        return getStringSetting(OUTCOME_TOPIC_ORDERS);
    }

    public String getOutcomeTopicSecurities() {
        return getStringSetting(OUTCOME_TOPIC_SECURITIES);
    }
}
