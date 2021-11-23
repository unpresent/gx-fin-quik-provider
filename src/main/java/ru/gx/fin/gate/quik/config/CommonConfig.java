package ru.gx.fin.gate.quik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import ru.gx.channels.ChannelMessageMode;
import ru.gx.channels.ChannelsConfiguration;
import ru.gx.channels.ChannelsConfigurator;
import ru.gx.channels.SerializeMode;
import ru.gx.fin.gate.quik.converters.QuikAllTradeFromOriginalQuikAllTradeConverter;
import ru.gx.fin.gate.quik.converters.QuikDealFromOriginalQuikDealConverter;
import ru.gx.fin.gate.quik.converters.QuikOrderFromOriginalQuikOrderConverter;
import ru.gx.fin.gate.quik.converters.QuikSecurityFromOriginalQuikSecurityConverter;
import ru.gx.fin.gate.quik.datacontrollers.*;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.fin.gate.quik.provider.out.*;
import ru.gx.kafka.upload.KafkaOutcomeTopicLoadingDescriptor;
import ru.gx.kafka.upload.SimpleKafkaOutcomeTopicsConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static lombok.AccessLevel.PROTECTED;

@EnableConfigurationProperties({ConfigurationPropertiesKafka.class, ConfigurationPropertiesQuik.class})
public class CommonConfig implements ChannelsConfigurator {
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
    // TODO: Должно создаваться в gx-fin-quik-gateway.
    @Bean
    public QuikProviderSettingsContainer quikProviderSettingsController() {
        return new QuikProviderSettingsContainer();
    }

    @Bean
    public QuikProvider provider() {
        return new QuikProvider();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Converters">
    @Bean
    public QuikAllTradeFromOriginalQuikAllTradeConverter quikAllTradeFromOriginalQuikAllTradeConverter() {
        return new QuikAllTradeFromOriginalQuikAllTradeConverter();
    }

    @Bean
    public QuikOrderFromOriginalQuikOrderConverter quikOrderFromOriginalQuikOrderConverter() {
        return new QuikOrderFromOriginalQuikOrderConverter();
    }

    @Bean
    public QuikDealFromOriginalQuikDealConverter quikDealFromOriginalQuikDealConverter() {
        return new QuikDealFromOriginalQuikDealConverter();
    }

    @Bean
    public QuikSecurityFromOriginalQuikSecurityConverter quikSecurityFromOriginalQuikSecurityConverter() {
        return new QuikSecurityFromOriginalQuikSecurityConverter();
    }    // </editor-fold>
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
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Kafka Producer">


    @SuppressWarnings("unchecked")
    @Override
    public void configureChannels(@NotNull ChannelsConfiguration configuration) {
        if (configuration instanceof SimpleKafkaOutcomeTopicsConfiguration) {
            final var config = (SimpleKafkaOutcomeTopicsConfiguration)configuration;
            config.getDescriptorsDefaults()
                    .setProducerProperties(producerProperties())
                    .setSerializeMode(SerializeMode.JsonString)
                    .setMessageMode(ChannelMessageMode.Package);

            config
                    .newDescriptor(this.settings.getOutcomeTopicAllTrades(), KafkaOutcomeTopicLoadingDescriptor.class)
                    .setDataObjectClass(QuikAllTrade.class)
                    .setDataPackageClass(QuikAllTradesPackage.class)
                    .init();

            config
                    .newDescriptor(this.settings.getOutcomeTopicOrders(), KafkaOutcomeTopicLoadingDescriptor.class)
                    .setDataObjectClass(QuikOrder.class)
                    .setDataPackageClass(QuikOrdersPackage.class)
                    .init();

            configuration
                    .newDescriptor(this.settings.getOutcomeTopicDeals(), KafkaOutcomeTopicLoadingDescriptor.class)
                    .setDataObjectClass(QuikDeal.class)
                    .setDataPackageClass(QuikOrdersPackage.class)
                    .init();

            configuration
                    .newDescriptor(this.settings.getOutcomeTopicSecurities(), KafkaOutcomeTopicLoadingDescriptor.class)
                    .setDataObjectClass(QuikSecurity.class)
                    .setDataPackageClass(QuikSecuritiesPackage.class)
                    .init();

        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
