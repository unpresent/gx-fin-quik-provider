package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чления анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends StandardQuikProviderDataController<AllTradesPackage> {
    @Autowired
    private OutcomeTopic outcomeTopicQuikAllTrades;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private KafkaTemplate<Long, AllTradesPackage> kafkaTemplate;

    @Autowired
    public QuikProviderAllTradesDataController() {
        super();
        this.init(50, 250);
    }

    @Override
    protected String outcomeTopicName() {
        return this.outcomeTopicQuikAllTrades.name();
    }



    @Override
    protected AllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        return this.getConnector().getAllTradesPackage(lastIndex, packageSize);
    }

    public static class OutcomeTopic extends NewTopic {
        public OutcomeTopic(String name, int numPartitions, short replicationFactor) {
            super(name, numPartitions, replicationFactor);
        }
    }
}
