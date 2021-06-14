package ru.gagarkin.gxfin.quik.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderAllTradesDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderDealsDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderOrdersDataController;
import ru.gagarkin.gxfin.quik.provider.QuikProviderSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Configuration
public class KafkaTopicConfig {
    @Value(value = "${kafka.server}")
    private String kafkaServer;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    @Autowired
    public QuikProviderAllTradesDataController.OutcomeTopic outcomeTopicQuikAllTrades(QuikProviderSettings settings) {
        return new QuikProviderAllTradesDataController.OutcomeTopic(settings.getOutcomeTopicAlltrades(), 1, (short) 1);
    }

    @Bean
    @Autowired
    public QuikProviderDealsDataController.OutcomeTopic outcomeTopicQuikDeals(QuikProviderSettings settings) {
        return new QuikProviderDealsDataController.OutcomeTopic(settings.getOutcomeTopicDeals(), 1, (short) 1);
    }

    @Bean
    @Autowired
    public QuikProviderOrdersDataController.OutcomeTopic outcomeTopicQuikOrders(QuikProviderSettings settings) {
        return new QuikProviderOrdersDataController.OutcomeTopic(settings.getOutcomeTopicOrders(), 1, (short) 1);
    }
}
