package ru.gxfin.quik.api;

public interface ProviderSettingsController {
    String QUIK_PIPE_NAME = "quik.pipe_name";
    String BUFFER_SIZE = "quik.buffer_size";
    String ATTEMPTS_ON_CONNECT = "quik.attempts_on_connect";
    String PAUSE_ON_CONNECT_MS = "quik.pause_on_connect_ms";
    String WAIT_ON_STOP_MS = "provider.wait_on_stop_ms";
    String MIN_TIME_PER_ITERATION_MS = "provider.min_time_per_iteration_ms";
    String INTERVAL_MANDATORY_READ_STATE_MS = "provider.interval_mandatory_read_state_ms";
    String TIMEOUT_LIFE_MS = "provider.timeout_life_ms";

    String OUTCOME_TOPIC_ALLTRADES = "kafka.outcome_topic.all_trades";
    String OUTCOME_TOPIC_DEALS = "kafka.outcome_topic.deals";
    String OUTCOME_TOPIC_ORDERS = "kafka.outcome_topic.orders";

    String getQuikPipeName();
    int getBufferSize();
    int getAttemptsOnConnect();
    int getPauseOnConnectMs();
    int getWaitOnStopMs();
    int getMinTimePerIterationMs();
    int getIntervalMandatoryReadStateMs();
    int getTimeoutLifeMs();

    String getOutcomeTopicAlltrades();
    String getOutcomeTopicDeals();
    String getOutcomeTopicOrders();
}
