package ru.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.model.internal.DealsPackage;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чтения сделок
 */
@Slf4j
public class QuikProviderDealsDataController
        extends StandardQuikProviderDataController<DealsPackage> {
    @Autowired
    public QuikProviderDealsDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicDeals();
    }

    @Override
    protected DealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        final var quikPackage = this.getConnector().getDealsPackage(lastIndex, packageSize);
        return new DealsPackage(quikPackage);
    }
}
