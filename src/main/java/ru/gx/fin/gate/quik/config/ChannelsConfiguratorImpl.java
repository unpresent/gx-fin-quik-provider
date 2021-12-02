package ru.gx.fin.gate.quik.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.gx.core.channels.ChannelMessageMode;
import ru.gx.core.channels.ChannelsConfiguration;
import ru.gx.core.channels.ChannelsConfigurator;
import ru.gx.core.channels.SerializeMode;
import ru.gx.core.kafka.upload.KafkaOutcomeTopicLoadingDescriptor;
import ru.gx.core.kafka.upload.SimpleKafkaOutcomeTopicsConfiguration;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.fin.gate.quik.provider.out.*;

import java.util.Properties;

import static lombok.AccessLevel.PROTECTED;

public class ChannelsConfiguratorImpl implements ChannelsConfigurator {
    @Value(value = "${service.kafka.server}")
    private String kafkaServer;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

    private Properties producerProperties() {
        final var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configureChannels(@NotNull final ChannelsConfiguration configuration) {
        if (configuration instanceof final SimpleKafkaOutcomeTopicsConfiguration config) {
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

            config
                    .newDescriptor(this.settings.getOutcomeTopicDeals(), KafkaOutcomeTopicLoadingDescriptor.class)
                    .setDataObjectClass(QuikDeal.class)
                    .setDataPackageClass(QuikOrdersPackage.class)
                    .init();

            config
                    .newDescriptor(this.settings.getOutcomeTopicSecurities(), KafkaOutcomeTopicLoadingDescriptor.class)
                    .setDataObjectClass(QuikSecurity.class)
                    .setDataPackageClass(QuikSecuritiesPackage.class)
                    .init();

        }
    }
}
