package ru.gx.fin.gate.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.model.internal.QuikOrder;
import ru.gx.fin.gate.quik.model.internal.QuikOrdersPackage;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderOrdersDataController
        extends StandardQuikProviderDataController<QuikOrder, QuikOrdersPackage> {
    public QuikProviderOrdersDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicOrders();
    }

    @Override
    protected QuikOrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var quikPackage = this.getConnector().getOrdersPackage(lastIndex, packageSize);
        return new QuikOrdersPackage(quikPackage);
    }
}
