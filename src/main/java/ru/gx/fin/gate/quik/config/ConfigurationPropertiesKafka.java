package ru.gx.fin.gate.quik.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public class ConfigurationPropertiesKafka {
    public static final String OUTCOME_TOPIC_ALL_TRADES = "kafka.outcome-topics.all-trades";
    public static final String OUTCOME_TOPIC_DEALS = "kafka.outcome-topics.deals";
    public static final String OUTCOME_TOPIC_ORDERS = "kafka.outcome-topics.orders";
    public static final String OUTCOME_TOPIC_SECURITIES = "kafka.outcome-topics.securities";

    @NestedConfigurationProperty
    private OutcomeTopics outcomeTopics = new OutcomeTopics();

    @Getter
    @Setter
    private static class OutcomeTopics {
        private String allTrades = "quikProviderAllTrades";
        private String deals = "quikProviderDeals";
        private String orders = "quikProviderOrders";
        private String securities = "quikProviderSecurities";
    }
}
