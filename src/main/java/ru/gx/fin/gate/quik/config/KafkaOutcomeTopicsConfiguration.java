package ru.gx.fin.gate.quik.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.gx.core.kafka.upload.AbstractKafkaOutcomeTopicsConfiguration;
import ru.gx.core.kafka.upload.KafkaOutcomeTopicUploadingDescriptor;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamAllTradesPackageDataPublishChannelApiV1;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamDealsPackageDataPublishChannelApiV1;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamOrdersPackageDataPublishChannelApiV1;
import ru.gx.fin.gate.quik.provider.channels.QuikProviderStreamSecuritiesPackageDataPublishChannelApiV1;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Configuration
public class KafkaOutcomeTopicsConfiguration extends AbstractKafkaOutcomeTopicsConfiguration {
    @NotNull
    private final String kafkaServer;

    @NotNull
    private final QuikProviderStreamAllTradesPackageDataPublishChannelApiV1 allTradesChannelApi;

    @NotNull
    private final QuikProviderStreamDealsPackageDataPublishChannelApiV1 dealsChannelApi;

    @NotNull
    private final QuikProviderStreamOrdersPackageDataPublishChannelApiV1 ordersChannelApi;

    @NotNull
    private final QuikProviderStreamSecuritiesPackageDataPublishChannelApiV1 securitiesChannelApi;

    public KafkaOutcomeTopicsConfiguration(
            @NotNull @Value("${service.kafka.server}") final String kafkaServer,
            @NotNull QuikProviderStreamAllTradesPackageDataPublishChannelApiV1 allTradesChannelApi,
            @NotNull QuikProviderStreamDealsPackageDataPublishChannelApiV1 dealsChannelApi,
            @NotNull QuikProviderStreamOrdersPackageDataPublishChannelApiV1 ordersChannelApi,
            @NotNull QuikProviderStreamSecuritiesPackageDataPublishChannelApiV1 securitiesChannelApi
    ) {
        super("kafka-outcome-config");
        this.kafkaServer = kafkaServer;
        this.allTradesChannelApi = allTradesChannelApi;
        this.dealsChannelApi = dealsChannelApi;
        this.ordersChannelApi = ordersChannelApi;
        this.securitiesChannelApi = securitiesChannelApi;
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
