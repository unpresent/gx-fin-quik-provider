package ru.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.model.internal.OrdersPackage;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderOrdersDataController
        extends StandardQuikProviderDataController<OrdersPackage> {
    @Autowired
    public QuikProviderOrdersDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicOrders();
    }

    @Override
    protected OrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        final var quikPackage = this.getConnector().getOrdersPackage(lastIndex, packageSize);
        return new OrdersPackage(quikPackage);
    }
}
