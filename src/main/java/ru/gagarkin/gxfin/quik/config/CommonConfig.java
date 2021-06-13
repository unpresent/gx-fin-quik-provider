package ru.gagarkin.gxfin.quik.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderAllTradesDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderDealsDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderOrdersDataController;
import ru.gagarkin.gxfin.quik.provider.QuikProvider;
import ru.gagarkin.gxfin.quik.provider.QuikProviderLifeController;
import ru.gagarkin.gxfin.quik.provider.QuikProviderSettings;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    @Bean
    @Autowired
    public QuikProviderSettings quikProviderSettings(ApplicationContext context) {
        return new QuikProviderSettings(context);
    }

    @Bean
    @Autowired
    public QuikConnector connector(QuikProviderSettings settings) {
        return new QuikConnector(settings.getQuikPipeName(), settings.getBufferSize());
    }

    @Bean
    public Provider provider() {
        return new QuikProvider();
    }

    @Bean
    @Autowired
    public List<ProviderDataController> providerDataControllers(Provider provider) {
        var result = new ArrayList<ProviderDataController>();
        result.add(new QuikProviderAllTradesDataController(provider));
        result.add(new QuikProviderDealsDataController(provider));
        result.add(new QuikProviderOrdersDataController(provider));
        return result;
    }

    @Bean
    @Autowired
    public QuikProviderLifeController providerLifeController(Provider provider) {
        return new QuikProviderLifeController(provider);
    }
}
