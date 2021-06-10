package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.gagarkin.gxfin.gate.quik.dto.OrdersPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderReadOrdersPackageEvent;

import java.io.IOException;

/**
 * Контролер чтения поручений
 */
@Slf4j
@Component
public class QuikProviderOrdersDataController
        extends StandardQuikProviderDataController<ProviderReadOrdersPackageEvent, OrdersPackage> {

    @Autowired
    public QuikProviderOrdersDataController(Provider provider) {
        super(provider);
        this.init(25, 500);
    }

    @Override
    protected OrdersPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        this.getProvider().registerStartExecution();
        try {
            return this.getConnector().getOrdersPackage(lastIndex, packageSize);
        } finally {
            this.getProvider().registerFinishExecution();
        }
    }

    @EventListener(ProviderReadOrdersPackageEvent.class)
    @Override
    public void onEvent(ProviderReadOrdersPackageEvent event) throws IOException, ProviderException, QuikConnectorException {
        super.onEvent(event);
    }

    @Override
    public ProviderReadOrdersPackageEvent createEvent(Object source) {
        return new ProviderReadOrdersPackageEvent(source);
    }
}
