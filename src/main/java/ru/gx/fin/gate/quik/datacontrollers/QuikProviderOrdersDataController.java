package ru.gx.fin.gate.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.data.NotAllowedObjectUpdateException;
import ru.gx.fin.gate.quik.converters.QuikOrderFromOriginalQuikOrderConverter;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.out.QuikOrder;
import ru.gx.fin.gate.quik.provider.out.QuikOrdersPackage;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderOrdersDataController
        extends AbstractQuikProviderDataController<QuikOrder, QuikOrdersPackage> {

    @Setter(value = AccessLevel.PROTECTED, onMethod_ = @Autowired)
    private QuikOrderFromOriginalQuikOrderConverter converter;

    public QuikProviderOrdersDataController() {
        super();
        this.init(25, 500);
    }

    @Override
    protected String outcomeTopicName() {
        return this.getSettings().getOutcomeTopicOrders();
    }

    @SneakyThrows(NotAllowedObjectUpdateException.class)
    @Override
    protected QuikOrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException {
        final var originalPackage = this.getConnector().getOrdersPackage(lastIndex, packageSize);
        final var result = new QuikOrdersPackage();
        this.converter.fillDtoCollectionFromSource(result.getObjects(), originalPackage.getObjects());
        return result;
    }
}
