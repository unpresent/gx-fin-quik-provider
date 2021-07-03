package ru.gxfin.quik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.gxfin.gate.quik.connector.QuikConnector;
import ru.gxfin.quik.api.Provider;
import ru.gxfin.quik.api.ProviderDataController;
import ru.gxfin.quik.api.ProviderSettingsController;
import ru.gxfin.quik.datacontrollers.QuikProviderAllTradesDataController;
import ru.gxfin.quik.datacontrollers.QuikProviderDealsDataController;
import ru.gxfin.quik.datacontrollers.QuikProviderOrdersDataController;
import ru.gxfin.quik.datacontrollers.QuikProviderSessionStateDataController;
import ru.gxfin.quik.provider.QuikProvider;
import ru.gxfin.quik.provider.QuikProviderLifeController;
import ru.gxfin.quik.provider.QuikProviderSettingsController;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

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
