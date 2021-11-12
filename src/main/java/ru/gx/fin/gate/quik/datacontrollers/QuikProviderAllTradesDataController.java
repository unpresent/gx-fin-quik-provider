package ru.gx.fin.gate.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikAllTradeFromOriginalQuikAllTradeConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.out.QuikAllTrade;
import ru.gx.fin.gate.quik.provider.out.QuikAllTradesPackage;

import java.io.IOException;

/**
 * Контролер чтения анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends AbstractQuikProviderDataController<QuikAllTrade, QuikAllTradesPackage> {

    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    private QuikAllTradeFromOriginalQuikAllTradeConverter converter;

    public QuikProviderAllTradesDataController() {
        super();
        this.init(50, 250);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicAllTrades();
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikAllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getAllTradesPackage(lastIndex, packageSize);
        final var result = new QuikAllTradesPackage();
        result.allCount = originalPackage.getQuikAllCount();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }
}
