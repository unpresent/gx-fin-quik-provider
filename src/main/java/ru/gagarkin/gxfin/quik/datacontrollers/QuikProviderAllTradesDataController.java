package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gagarkin.gxfin.gate.quik.dto.AllTradesPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чления анонимных сделок
 */
@Slf4j
public class QuikProviderAllTradesDataController
        extends StandardQuikProviderDataController<AllTradesPackage> {

    @Autowired
    public QuikProviderAllTradesDataController(Provider provider) {
        super();
        this.init(50, 250);
    }

    @Override
    protected AllTradesPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        return this.getConnector().getAllTradesPackage(lastIndex, packageSize);
    }

}
