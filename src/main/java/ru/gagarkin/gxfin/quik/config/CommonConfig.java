package ru.gagarkin.gxfin.quik.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.api.ProviderSettingsController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderAllTradesDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderDealsDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderOrdersDataController;
import ru.gagarkin.gxfin.quik.datacontrollers.QuikProviderSessionStateDataController;
import ru.gagarkin.gxfin.quik.provider.QuikProvider;
import ru.gagarkin.gxfin.quik.provider.QuikProviderLifeController;
import ru.gagarkin.gxfin.quik.provider.QuikProviderSettingsController;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    @Bean
    @Autowired
    public ProviderSettingsController quikProviderSettings(ApplicationContext context) {
        return new QuikProviderSettingsController(context);
    }

    @Bean
    @Autowired
    public QuikConnector connector(ProviderSettingsController settings) {
        return new QuikConnector(settings.getQuikPipeName(), settings.getBufferSize());
    }

    @Bean
    public Provider provider() {
        return new QuikProvider();
    }

    @Bean
    public QuikProviderLifeController providerLifeController() {
        return new QuikProviderLifeController();
    }

    @Bean
    public QuikProviderAllTradesDataController quikProviderAllTradesDataController() {
        return new QuikProviderAllTradesDataController();
    }

    @Bean
    public QuikProviderDealsDataController quikProviderDealsDataController() {
        return new QuikProviderDealsDataController();
    }

    @Bean
    public QuikProviderOrdersDataController quikProviderOrdersDataController() {
        return new QuikProviderOrdersDataController();
    }

    @Bean
    public QuikProviderSessionStateDataController quikProviderSessionStateDataController() {
        return new QuikProviderSessionStateDataController();
    }

    @SuppressWarnings("SpringConfigurationProxyMethods")
    @Bean
    public List<ProviderDataController> providerDataControllers() {
        final var result = new ArrayList<ProviderDataController>();
        result.add(quikProviderAllTradesDataController());
        result.add(quikProviderDealsDataController());
        result.add(quikProviderOrdersDataController());
        result.add(quikProviderSessionStateDataController());
        return result;
    }
}
