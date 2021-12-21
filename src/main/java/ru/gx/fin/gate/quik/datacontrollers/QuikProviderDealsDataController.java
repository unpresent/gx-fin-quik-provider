package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikDealFromOriginalQuikDealConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.config.QuikProviderChannelNames;
import ru.gx.fin.gate.quik.provider.messages.QuikProviderStreamDealsPackageDataPublish;
import ru.gx.fin.gate.quik.provider.out.QuikDeal;
import ru.gx.fin.gate.quik.provider.out.QuikDealsPackage;

import java.io.IOException;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения сделок
 */
@Slf4j
public class QuikProviderDealsDataController
        extends AbstractQuikProviderDataController<QuikProviderStreamDealsPackageDataPublish, QuikDeal, QuikDealsPackage> {

    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikDealFromOriginalQuikDealConverter converter;

    public QuikProviderDealsDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return QuikProviderChannelNames.Streams.DEALS_V1;
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikDealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getDealsPackage(lastIndex, packageSize);
        final var result = new QuikDealsPackage();
        result.allCount = originalPackage.getQuikAllCount();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }
}
