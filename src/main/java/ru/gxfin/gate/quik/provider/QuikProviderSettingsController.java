package ru.gxfin.gate.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.gxfin.common.settings.AbstractSettingsController;
import ru.gxfin.common.settings.UnknownApplicationSettingException;

@Slf4j
public class QuikProviderSettingsController extends AbstractSettingsController {
    String QUIK_PIPE_NAME = "quik.pipe_name";
    String BUFFER_SIZE = "quik.buffer_size";
    String ATTEMPTS_ON_CONNECT = "quik.attempts_on_connect";
    String PAUSE_ON_CONNECT_MS = "quik.pause_on_connect_ms";
    String WAIT_ON_STOP_MS = "provider.wait_on_stop_ms";
    String WAIT_ON_RESTART_MS = "provider.wait_on_restarts_ms";
    String MIN_TIME_PER_ITERATION_MS = "provider.min_time_per_iteration_ms";
    String INTERVAL_MANDATORY_READ_STATE_MS = "provider.interval_mandatory_read_state_ms";
    String TIMEOUT_LIFE_MS = "provider.timeout_life_ms";

    String OUTCOME_TOPIC_ALLTRADES = "kafka.outcome_topic.all_trades";
    String OUTCOME_TOPIC_DEALS = "kafka.outcome_topic.deals";
    String OUTCOME_TOPIC_ORDERS = "kafka.outcome_topic.orders";
    String OUTCOME_TOPIC_SECURITIES = "kafka.outcome_topic.securities";

    @Autowired
    public QuikProviderSettingsController(ApplicationContext context) throws UnknownApplicationSettingException {
        super(context);

        // TODO: Переписать на чтение настроек
        loadStringSetting(QUIK_PIPE_NAME);
        loadIntegerSetting(BUFFER_SIZE);
        loadIntegerSetting(ATTEMPTS_ON_CONNECT);
        loadIntegerSetting(PAUSE_ON_CONNECT_MS);
        loadIntegerSetting(WAIT_ON_STOP_MS);
        loadIntegerSetting(WAIT_ON_RESTART_MS);
        loadIntegerSetting(MIN_TIME_PER_ITERATION_MS);
        loadIntegerSetting(INTERVAL_MANDATORY_READ_STATE_MS);
        loadIntegerSetting(TIMEOUT_LIFE_MS);

        loadStringSetting(OUTCOME_TOPIC_ALLTRADES);
        loadStringSetting(OUTCOME_TOPIC_DEALS);
        loadStringSetting(OUTCOME_TOPIC_ORDERS);
        loadStringSetting(OUTCOME_TOPIC_SECURITIES);
    }

    public String getQuikPipeName() {
        return (String) this.getSetting(QUIK_PIPE_NAME);
    }

    public int getBufferSize() {
        return (Integer) this.getSetting(BUFFER_SIZE);
    }

    public int getAttemptsOnConnect() {
        return (Integer) this.getSetting(ATTEMPTS_ON_CONNECT);
    }

    public int getPauseOnConnectMs() {
        return (Integer) this.getSetting(PAUSE_ON_CONNECT_MS);
    }

    public int getWaitOnStopMs() {
        return (Integer) this.getSetting(WAIT_ON_STOP_MS);
    }

    public int getWaitOnRestartMs() {
        return (Integer) this.getSetting(WAIT_ON_RESTART_MS);
    }

    public int getMinTimePerIterationMs() {
        return (Integer) this.getSetting(MIN_TIME_PER_ITERATION_MS);
    }

    public int getIntervalMandatoryReadStateMs() {
        return (Integer) this.getSetting(INTERVAL_MANDATORY_READ_STATE_MS);
    }

    public int getTimeoutLifeMs() {
        return (Integer) this.getSetting(TIMEOUT_LIFE_MS);
    }

    public String getOutcomeTopicAlltrades() {
        return (String) this.getSetting(OUTCOME_TOPIC_ALLTRADES);
    }

    public String getOutcomeTopicDeals() {
        return (String) this.getSetting(OUTCOME_TOPIC_DEALS);
    }

    public String getOutcomeTopicOrders() {
        return (String) this.getSetting(OUTCOME_TOPIC_ORDERS);
    }

    public String getOutcomeTopicSecurities() {
        return (String) this.getSetting(OUTCOME_TOPIC_SECURITIES);
    }
}
