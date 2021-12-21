package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikAllTradeFromOriginalQuikAllTradeConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.config.QuikProviderChannelNames;
import ru.gx.fin.gate.quik.provider.messages.QuikProviderStreamAllTradesPackageDataPublish;
import ru.gx.fin.gate.quik.provider.out.QuikAllTrade;
import ru.gx.fin.gate.quik.provider.out.QuikAllTradesPackage;

import java.io.IOException;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends AbstractQuikProviderDataController<QuikProviderStreamAllTradesPackageDataPublish, QuikAllTrade, QuikAllTradesPackage> {

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikAllTradeFromOriginalQuikAllTradeConverter converter;

    public QuikProviderAllTradesDataController() {
        super();
        this.init(50, 250);
    }

    @Override
    protected String outcomeTopicName() {
        return QuikProviderChannelNames.Streams.ALL_TRADES_V1;
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
