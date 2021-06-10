package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.gagarkin.gxfin.gate.quik.dto.DealsPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderReadDealsPackageEvent;

import java.io.IOException;

/**
 * Контролер чтения сделок
 */
@Slf4j
@Component
public class QuikProviderDealsDataController
        extends StandardQuikProviderDataController<ProviderReadDealsPackageEvent, DealsPackage> {

    @Autowired
    public QuikProviderDealsDataController(Provider provider) {
        super(provider);
        this.init(25, 500);
    }

    @Override
    protected DealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        this.getProvider().registerStartExecution();
        try {
            var result = this.getConnector().getDealsPackage(lastIndex, packageSize);
            if (result.rows.length > 0)
                log.info("settleDate = {}", result.rows[0].settleDate);
            return result;
        } finally {
            this.getProvider().registerFinishExecution();
        }
    }

    @EventListener(ProviderReadDealsPackageEvent.class)
    @Override
    public void onEvent(ProviderReadDealsPackageEvent event) throws IOException, ProviderException, QuikConnectorException {
        super.onEvent(event);
    }

    @Override
    public ProviderReadDealsPackageEvent createEvent(Object source) {
        return new ProviderReadDealsPackageEvent(source);
    }
}
