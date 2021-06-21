package ru.gagarkin.gxfin.quik.api;

public interface ProviderSettingsController {
    String QUIK_PIPE_NAME = "quik_pipe_name";
    String BUFFER_SIZE = "buffer_size";
    String ATTEMPTS_ON_CONNECT = "attempts_on_connect";
    String PAUSE_ON_CONNECT_MS = "pause_on_connect_ms";
    String WAIT_ON_STOP_MS = "wait_on_stop_ms";
    String MIN_TIME_PER_ITERATION_MS = "min_time_per_iteration_ms";
    String INTERVAL_MANDATORY_READ_STATE_MS = "interval_mandatory_read_state_ms";
    String TIMEOUT_LIFE_MS = "timeout_life_ms";

    String OUTCOME_TOPIC_ALLTRADES = "outcome_topic_alltrades";
    String OUTCOME_TOPIC_DEALS = "outcome_topic_deals";
    String OUTCOME_TOPIC_ORDERS = "outcome_topic_orders";

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
