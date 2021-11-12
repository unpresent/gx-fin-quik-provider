package ru.gx.fin.gate.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikAllTradeFromOriginalQuikAllTradeConverter;
import ru.gx.fin.gate.quik.converters.QuikDealFromOriginalQuikDealConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.out.QuikDeal;
import ru.gx.fin.gate.quik.provider.out.QuikDealsPackage;

import java.io.IOException;

/**
 * Контролер чтения сделок
 */
@Slf4j
public class QuikProviderDealsDataController
        extends AbstractQuikProviderDataController<QuikDeal, QuikDealsPackage> {

    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    private QuikDealFromOriginalQuikDealConverter converter;

    public QuikProviderDealsDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicDeals();
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikDealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getDealsPackage(lastIndex, packageSize);
        final var result = new QuikDealsPackage();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }
}
