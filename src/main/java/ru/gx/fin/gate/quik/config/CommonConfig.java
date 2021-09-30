package ru.gx.fin.gate.quik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.datacontrollers.*;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderLifeController;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.settings.SimpleSettingsController;
import ru.gx.worker.SimpleWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class CommonConfig {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Common">
    @Value("${service.name}")
    private String serviceName;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Provider & Settings">
    @Bean
    public SimpleSettingsController simpleSettingsController() {
        return new SimpleSettingsController(this.serviceName);
    }

    @Bean
    public SimpleWorker simpleWorker() {
        return new SimpleWorker(this.serviceName);
    }

    @Bean
    public QuikProviderSettingsContainer quikProviderSettingsController() {
        return new QuikProviderSettingsContainer();
    }

    @Bean
    @Autowired
    public QuikConnector connector(ObjectMapper objectMapper, QuikProviderSettingsContainer settings) {
        return new QuikConnector(objectMapper, settings.getQuikPipeName(), settings.getBufferSize());
    }

    @Bean
    public QuikProvider provider() {
        return new QuikProvider();
    }

    @Bean
    public QuikProviderLifeController providerLifeController() {
        return new QuikProviderLifeController();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="DataControllers">
    @Bean
    public QuikProviderAllTradesDataController quikProviderAllTradesDataController() {
        return new QuikProviderAllTradesDataController();
    }

    @Bean
    public QuikProviderDealsDataController quikProviderDealsDataController() {
        return new QuikProviderDealsDataController();
    }

    @Bean
    public QuikProviderOrdersDataController quikProviderOrdersDataController() {
        return new QuikProviderOrdersDataController();
    }

    @Bean
    public QuikProviderSecuritiesDataController quikProviderSecuritiesDataController() {
        return new QuikProviderSecuritiesDataController();
    }

    @Bean
    public QuikProviderSessionStateDataController quikProviderSessionStateDataController() {
        return new QuikProviderSessionStateDataController();
    }

    @SuppressWarnings("SpringConfigurationProxyMethods")
    @Bean
    public List<ProviderDataController> providerDataControllers() {
        final var result = new ArrayList<ProviderDataController>();
        result.add(quikProviderAllTradesDataController());
        result.add(quikProviderDealsDataController());
        result.add(quikProviderOrdersDataController());
        result.add(quikProviderSecuritiesDataController());
        result.add(quikProviderSessionStateDataController());
        return result;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Kafka Producer">
    @Value(value = "${kafka.server}")
    private String kafkaServer;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final var configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaProducer<Long, String> producer() {
        final var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaProducer<>(props);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
