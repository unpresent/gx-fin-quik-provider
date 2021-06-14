package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.gate.quik.dto.Order;
import ru.gagarkin.gxfin.gate.quik.dto.OrdersPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderOrdersDataController
        extends StandardQuikProviderDataController<OrdersPackage> {
    @Autowired
    private OutcomeTopic outcomeTopicQuikOrders;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private KafkaTemplate<Long, OrdersPackage> kafkaTemplate;

    @Autowired
    public QuikProviderOrdersDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.outcomeTopicQuikOrders.name();
    }

    @Override
    protected OrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        return this.getConnector().getOrdersPackage(lastIndex, packageSize);
    }

    public static class OutcomeTopic extends NewTopic {
        public OutcomeTopic(String name, int numPartitions, short replicationFactor) {
            super(name, numPartitions, replicationFactor);
        }
    }
}
