package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gagarkin.gxfin.gate.quik.dto.DealsPackage;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;

import java.io.IOException;

/**
 * Контролер чтения сделок
 */
@Slf4j
public class QuikProviderDealsDataController
        extends StandardQuikProviderDataController<DealsPackage> {

    @Autowired
    public QuikProviderDealsDataController(Provider provider) {
        super();
        this.init(25, 500);
    }

    @Override
    protected DealsPackage getPackage(long lastIndex, int packageSize) throws IOException, QuikConnectorException, ProviderException {
        var result = this.getConnector().getDealsPackage(lastIndex, packageSize);
        if (result.rows.length > 0)
            log.info("settleDate = {}", result.rows[0].settleDate);
        return result;
    }
}
