package ru.gxfin.gate.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.gate.quik.model.internal.SecuritiesPackage;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderSecuritiesDataController
        extends StandardQuikProviderDataController<SecuritiesPackage> {
    @Autowired
    public QuikProviderSecuritiesDataController() {
        super();
        this.init(10, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicSecurities();
    }

    @Override
    protected SecuritiesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var quikPackage = this.getConnector().getSecuritiesPackage(lastIndex, packageSize);
        return new SecuritiesPackage(quikPackage);
    }
}
