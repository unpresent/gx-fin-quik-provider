package ru.gx.fin.gate.quik.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ru.gx.core.kafka.upload.AbstractKafkaOutcomeTopicsConfiguration;
import ru.gx.core.kafka.upload.KafkaOutcomeTopicUploadingDescriptor;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamAllTradesPackageDataPublishChannelApiV1;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamDealsPackageDataPublishChannelApiV1;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamOrdersPackageDataPublishChannelApiV1;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamSecuritiesPackageDataPublishChannelApiV1;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static lombok.AccessLevel.PROTECTED;

public class KafkaOutcomeTopicsConfiguration extends AbstractKafkaOutcomeTopicsConfiguration {
    @Value(value = "${service.kafka.server}")
    private String kafkaServer;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderStreamAllTradesPackageDataPublishChannelApiV1 allTradesChannelApi;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderStreamDealsPackageDataPublishChannelApiV1 dealsChannelApi;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderStreamOrdersPackageDataPublishChannelApiV1 ordersChannelApi;

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderStreamSecuritiesPackageDataPublishChannelApiV1 securitiesChannelApi;

    public KafkaOutcomeTopicsConfiguration(@NotNull String configurationName) {
        super(configurationName);
    }

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void init() {
        this.getDescriptorsDefaults()
                .setProducerProperties(producerProperties());

        this
                .newDescriptor(this.ordersChannelApi, KafkaOutcomeTopicUploadingDescriptor.class)
                .init();

        this
                .newDescriptor(this.dealsChannelApi, KafkaOutcomeTopicUploadingDescriptor.class)
                .init();

        this
                .newDescriptor(this.securitiesChannelApi, KafkaOutcomeTopicUploadingDescriptor.class)
                .init();

        this
                .newDescriptor(this.allTradesChannelApi, KafkaOutcomeTopicUploadingDescriptor.class)
                .init();
    }

    protected Properties producerProperties() {
        final var props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }
}
