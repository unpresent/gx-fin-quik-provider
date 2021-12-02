package ru.gx.fin.gate.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikSecurityFromOriginalQuikSecurityConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.out.QuikSecuritiesPackage;
import ru.gx.fin.gate.quik.provider.out.QuikSecurity;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderSecuritiesDataController
        extends AbstractQuikProviderDataController<QuikSecurity, QuikSecuritiesPackage> {

    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    private QuikSecurityFromOriginalQuikSecurityConverter converter;

    public QuikProviderSecuritiesDataController() {
        super();
        this.init(10, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicSecurities();
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikSecuritiesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getSecuritiesPackage(lastIndex, packageSize);
        final var result = new QuikSecuritiesPackage();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }
}
