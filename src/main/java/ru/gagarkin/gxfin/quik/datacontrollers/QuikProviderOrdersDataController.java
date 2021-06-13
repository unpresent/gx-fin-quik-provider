package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gagarkin.gxfin.gate.quik.dto.OrdersPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
public class QuikProviderOrdersDataController
        extends StandardQuikProviderDataController<OrdersPackage> {

    @Autowired
    public QuikProviderOrdersDataController(Provider provider) {
        super();
        this.init(25, 500);
    }

    @Override
    protected OrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        return this.getConnector().getOrdersPackage(lastIndex, packageSize);
    }
}
