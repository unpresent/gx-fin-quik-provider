package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ru.gagarkin.gxfin.gate.quik.data.internal.DealsPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чтения сделок
 */
@Slf4j
public class QuikProviderDealsDataController
        extends StandardQuikProviderDataController<DealsPackage> {
    @Autowired
    private OutcomeTopic outcomeTopicQuikDeals;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private KafkaTemplate<Long, DealsPackage> kafkaTemplate;

    @Autowired
    public QuikProviderDealsDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.outcomeTopicQuikDeals.name();
    }

    @Override
    protected DealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        final var quikPackage = this.getConnector().getDealsPackage(lastIndex, packageSize);
        return new DealsPackage(quikPackage);
    }

    public static class OutcomeTopic extends NewTopic {
        public OutcomeTopic(String name, int numPartitions, short replicationFactor) {
            super(name, numPartitions, replicationFactor);
        }
    }
}
