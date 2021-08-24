package ru.gxfin.gate.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.gate.quik.model.internal.QuikAllTrade;
import ru.gxfin.gate.quik.model.internal.QuikAllTradesPackage;

import java.io.IOException;

/**
 * Контролер чтения анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends StandardQuikProviderDataController<QuikAllTrade, QuikAllTradesPackage> {

    @Autowired
    public QuikProviderAllTradesDataController() {
        super();
        this.init(50, 250);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicAlltrades();
    }

    @Override
    protected QuikAllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var quikPackage = this.getConnector().getAllTradesPackage(lastIndex, packageSize);
        return new QuikAllTradesPackage(quikPackage);
    }
}
