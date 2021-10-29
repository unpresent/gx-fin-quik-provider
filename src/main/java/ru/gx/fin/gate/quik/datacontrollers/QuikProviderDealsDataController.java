package ru.gx.fin.gate.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.model.internal.QuikDeal;
import ru.gx.fin.gate.quik.model.internal.QuikDealsPackage;

import java.io.IOException;

/**
 * Контролер чтения сделок
 */
@Slf4j
public class QuikProviderDealsDataController
        extends AbstractQuikProviderDataController<QuikDeal, QuikDealsPackage> {
    public QuikProviderDealsDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicDeals();
    }

    @Override
    protected QuikDealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var quikPackage = this.getConnector().getDealsPackage(lastIndex, packageSize);
        return new QuikDealsPackage(quikPackage);
    }
}
