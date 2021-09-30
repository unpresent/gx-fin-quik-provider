package ru.gx.fin.gate.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.model.internal.QuikAllTrade;
import ru.gx.fin.gate.quik.model.internal.QuikAllTradesPackage;

import java.io.IOException;

/**
 * Контролер чтения анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends StandardQuikProviderDataController<QuikAllTrade, QuikAllTradesPackage> {
    public QuikProviderAllTradesDataController() {
        super();
        this.init(50, 250);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicAllTrades();
    }

    @Override
    protected QuikAllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var quikPackage = this.getConnector().getAllTradesPackage(lastIndex, packageSize);
        return new QuikAllTradesPackage(quikPackage);
    }
}
