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
