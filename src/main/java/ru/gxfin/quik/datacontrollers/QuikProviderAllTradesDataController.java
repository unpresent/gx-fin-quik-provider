package ru.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.model.internal.AllTradesPackage;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чления анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends StandardQuikProviderDataController<AllTradesPackage> {

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
    protected AllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        final var quikPackage = this.getConnector().getAllTradesPackage(lastIndex, packageSize);
        return new AllTradesPackage(quikPackage);
    }
}
