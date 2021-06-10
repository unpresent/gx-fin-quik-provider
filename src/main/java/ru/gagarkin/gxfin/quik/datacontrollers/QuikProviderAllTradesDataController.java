package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderReadAllTradesPackageEvent;

import java.io.IOException;

/**
 * Контролер чления анонимных сделок
 */
@Slf4j
@Component
public class QuikProviderAllTradesDataController
        extends StandardQuikProviderDataController<ProviderReadAllTradesPackageEvent, AllTradesPackage> {

    @Autowired
    public QuikProviderAllTradesDataController(Provider provider) {
        super(provider);
        this.init(50, 250);
    }

    @Override
    protected AllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        this.getProvider().registerStartExecution();
        try {
            return this.getConnector().getAllTradesPackage(lastIndex, packageSize);
        } finally {
            this.getProvider().registerFinishExecution();
        }
    }

    @EventListener(ProviderReadAllTradesPackageEvent.class)
    @Override
    public void onEvent(ProviderReadAllTradesPackageEvent event) throws IOException, ProviderException, QuikConnectorException {
        super.onEvent(event);
    }

    @Override
    public ProviderReadAllTradesPackageEvent createEvent(Object source) {
        return new ProviderReadAllTradesPackageEvent(source);
    }

}
