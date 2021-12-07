package ru.gx.fin.gate.quik.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "service.kafka")
@Getter
@Setter
public class ConfigurationPropertiesServiceKafka {
    public static final String OUTCOME_TOPIC_ALL_TRADES = "service.kafka.outcome-topics.all-trades";
    public static final String OUTCOME_TOPIC_DEALS = "service.kafka.outcome-topics.deals";
    public static final String OUTCOME_TOPIC_ORDERS = "service.kafka.outcome-topics.orders";
    public static final String OUTCOME_TOPIC_SECURITIES = "service.kafka.outcome-topics.securities";

    public static final String OUTCOME_TOPIC_ALL_TRADES_DEFAULT_VALUE = "quik.provider-all-trades";
    public static final String OUTCOME_TOPIC_DEALS_DEFAULT_VALUE = "quik.provider-deals";
    public static final String OUTCOME_TOPIC_ORDERS_DEFAULT_VALUE = "quik.provider-orders";
    public static final String OUTCOME_TOPIC_SECURITIES_DEFAULT_VALUE = "quik.provider-securities";

    @NestedConfigurationProperty
    private OutcomeTopics outcomeTopics = new OutcomeTopics();

    @Getter
    @Setter
    private static class OutcomeTopics {
        private String allTrades = OUTCOME_TOPIC_ALL_TRADES_DEFAULT_VALUE;
        private String deals = OUTCOME_TOPIC_DEALS_DEFAULT_VALUE;
        private String orders = OUTCOME_TOPIC_ORDERS_DEFAULT_VALUE;
        private String securities = OUTCOME_TOPIC_SECURITIES_DEFAULT_VALUE;
    }
}
