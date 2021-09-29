package ru.gx.fin.gate.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.model.internal.QuikSecuritiesPackage;
import ru.gx.fin.gate.quik.model.internal.QuikSecurity;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderSecuritiesDataController
        extends StandardQuikProviderDataController<QuikSecurity, QuikSecuritiesPackage> {
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
    protected QuikSecuritiesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var quikPackage = this.getConnector().getSecuritiesPackage(lastIndex, packageSize);
        return new QuikSecuritiesPackage(quikPackage);
    }
}
