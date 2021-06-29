package ru.gagarkin.gxfin.quik.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;
import ru.gagarkin.gxfin.quik.api.ProviderSettingsController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderAllTradesDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderDealsDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderOrdersDataController;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value(value = "${kafka.server}")
    private String kafkaServer;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final var configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    @Autowired
    public QuikProviderAllTradesDataController.OutcomeTopic outcomeTopicQuikAllTrades(ProviderSettingsController settings) {
        return new QuikProviderAllTradesDataController.OutcomeTopic(settings.getOutcomeTopicAlltrades(), 1, (short) 1);
    }

    @Bean
    @Autowired
    public QuikProviderDealsDataController.OutcomeTopic outcomeTopicQuikDeals(ProviderSettingsController settings) {
        return new QuikProviderDealsDataController.OutcomeTopic(settings.getOutcomeTopicDeals(), 1, (short) 1);
    }

    @Bean
    @Autowired
    public QuikProviderOrdersDataController.OutcomeTopic outcomeTopicQuikOrders(ProviderSettingsController settings) {
        return new QuikProviderOrdersDataController.OutcomeTopic(settings.getOutcomeTopicOrders(), 1, (short) 1);
    }
}
