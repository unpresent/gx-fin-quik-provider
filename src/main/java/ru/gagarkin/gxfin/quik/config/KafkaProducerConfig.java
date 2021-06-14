package ru.gagarkin.gxfin.quik.config;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.gate.quik.dto.DealsPackage;
import ru.gagarkin.gxfin.gate.quik.dto.Order;
import ru.gagarkin.gxfin.gate.quik.dto.OrdersPackage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${kafka.server}")
    private String kafkaServer;

    @Value("${kafka.producer.id}")
    private String kafkaProducerId;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> result = new HashMap<>();
        result.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        result.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        result.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        result.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaProducerId);
        return result;
    }

    @Bean
    public ProducerFactory<Long, AllTradesPackage> producerAllTradesFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<Long, DealsPackage> producerDealsFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public ProducerFactory<Long, OrdersPackage> producerOrdersFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Long, AllTradesPackage> kafkaAllTradesTemplate() {
        KafkaTemplate<Long, AllTradesPackage> template = new KafkaTemplate<>(producerAllTradesFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }

    @Bean
    public KafkaTemplate<Long, DealsPackage> kafkaDealsTemplate() {
        KafkaTemplate<Long, DealsPackage> template = new KafkaTemplate<>(producerDealsFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }

    @Bean
    public KafkaTemplate<Long, OrdersPackage> kafkaOrdersTemplate() {
        KafkaTemplate<Long, OrdersPackage> template = new KafkaTemplate<>(producerOrdersFactory());
        template.setMessageConverter(new StringJsonMessageConverter());
        return template;
    }
}