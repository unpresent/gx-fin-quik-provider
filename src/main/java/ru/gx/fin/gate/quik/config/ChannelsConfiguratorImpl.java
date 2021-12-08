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
import ru.gx.core.redis.upload.RedisOutcomeCollectionLoadingDescriptor;
import ru.gx.core.redis.upload.SimpleRedisOutcomeCollectionsConfiguration;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.fin.gate.quik.provider.config.QuikProviderChannelsNames;
import ru.gx.fin.gate.quik.provider.out.*;

import java.util.Properties;

import static lombok.AccessLevel.PROTECTED;

public class ChannelsConfiguratorImpl implements ChannelsConfigurator {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    @Value(value = "${service.kafka.server}")
    private String kafkaServer;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="implements ChannelsConfigurator">
    @Override
    public void configureChannels(@NotNull final ChannelsConfiguration configuration) {
        if (configuration instanceof final SimpleKafkaOutcomeTopicsConfiguration config) {
            this.configureSimpleKafkaOutcomeTopicsConfiguration(config);
        } else if (configuration instanceof final SimpleRedisOutcomeCollectionsConfiguration config) {
            this.configureSimpleRedisOutcomeCollectionsConfiguration(config);
        }
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Kafka configuration">
    protected Properties producerProperties() {
        final var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @SuppressWarnings("unchecked")
    protected void configureSimpleKafkaOutcomeTopicsConfiguration(@NotNull final SimpleKafkaOutcomeTopicsConfiguration configuration) {
        configuration.getDescriptorsDefaults()
                .setProducerProperties(producerProperties())
                .setSerializeMode(SerializeMode.JsonString)
                .setMessageMode(ChannelMessageMode.Package);

        configuration
                .newDescriptor(QuikProviderChannelsNames.Streams.ALL_TRADES, KafkaOutcomeTopicLoadingDescriptor.class)
                .setDataObjectClass(QuikAllTrade.class)
                .setDataPackageClass(QuikAllTradesPackage.class)
                .init();

        configuration
                .newDescriptor(QuikProviderChannelsNames.Streams.ORDERS, KafkaOutcomeTopicLoadingDescriptor.class)
                .setDataObjectClass(QuikOrder.class)
                .setDataPackageClass(QuikOrdersPackage.class)
                .init();

        configuration
                .newDescriptor(QuikProviderChannelsNames.Streams.DEALS, KafkaOutcomeTopicLoadingDescriptor.class)
                .setDataObjectClass(QuikDeal.class)
                .setDataPackageClass(QuikOrdersPackage.class)
                .init();

        configuration
                .newDescriptor(QuikProviderChannelsNames.Streams.SECURITIES, KafkaOutcomeTopicLoadingDescriptor.class)
                .setDataObjectClass(QuikSecurity.class)
                .setDataPackageClass(QuikSecuritiesPackage.class)
                .init();

    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Redis configuration">
    protected void configureSimpleRedisOutcomeCollectionsConfiguration(@NotNull final SimpleRedisOutcomeCollectionsConfiguration configuration) {
        configuration.getDescriptorsDefaults()
                .setMessageMode(ChannelMessageMode.Object);

        configuration
                .newDescriptor(QuikProviderChannelsNames.Snapshots.SECURITIES, RedisOutcomeCollectionLoadingDescriptor.class)
                .setPriority(0)
                .init();

    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------

}
