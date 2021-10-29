package ru.gx.fin.gate.quik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.datacontrollers.*;
import ru.gx.fin.gate.quik.descriptors.QuikAllTradesUploadingDescriptor;
import ru.gx.fin.gate.quik.descriptors.QuikDealsUploadingDescriptor;
import ru.gx.fin.gate.quik.descriptors.QuikOrdersUploadingDescriptor;
import ru.gx.fin.gate.quik.descriptors.QuikSecuritiesUploadingDescriptor;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.kafka.TopicMessageMode;
import ru.gx.kafka.upload.OutcomeTopicsConfiguration;
import ru.gx.kafka.upload.OutcomeTopicsConfigurator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static lombok.AccessLevel.PROTECTED;

public class CommonConfig implements OutcomeTopicsConfigurator {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Common">
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Provider & Settings">
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

    private Properties producerProperties() {
        final var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public KafkaProducer<Long, String> producer() {
        return new KafkaProducer<>(producerProperties());
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Kafka Producer">

    @Override
    public void configureOutcomeTopics(@NotNull OutcomeTopicsConfiguration configuration) {
        final var defaults = configuration.getDescriptorsDefaults()
                .setTopicMessageMode(TopicMessageMode.PACKAGE)
                .setProducerProperties(producerProperties());

        configuration
                .register(
                        new QuikAllTradesUploadingDescriptor(this.settings.getOutcomeTopicAllTrades(), defaults)
                )
                .register(
                        new QuikOrdersUploadingDescriptor(this.settings.getOutcomeTopicOrders(), defaults)
                )
                .register(
                        new QuikDealsUploadingDescriptor(this.settings.getOutcomeTopicDeals(), defaults)
                )
                .register(
                        new QuikSecuritiesUploadingDescriptor(this.settings.getOutcomeTopicSecurities(), defaults)
                );

    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
