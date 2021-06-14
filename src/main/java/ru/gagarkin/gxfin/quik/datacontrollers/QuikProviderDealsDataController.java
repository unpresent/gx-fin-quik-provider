package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.gate.quik.dto.DealsPackage;
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
        var result = this.getConnector().getDealsPackage(lastIndex, packageSize);
        if (result.rows.length > 0)
            log.info("settleDate = {}", result.rows[0].settleDate);
        return result;
    }

    public static class OutcomeTopic extends NewTopic {
        public OutcomeTopic(String name, int numPartitions, short replicationFactor) {
            super(name, numPartitions, replicationFactor);
        }
    }
}
