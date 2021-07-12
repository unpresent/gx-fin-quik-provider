package ru.gxfin.gate.quik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.gxfin.gate.quik.connector.QuikConnector;
import ru.gxfin.gate.quik.api.Provider;
import ru.gxfin.gate.quik.api.ProviderDataController;
import ru.gxfin.gate.quik.api.ProviderLifeController;
import ru.gxfin.gate.quik.api.ProviderSettingsController;
import ru.gxfin.gate.quik.datacontrollers.*;
import ru.gxfin.gate.quik.provider.QuikProvider;
import ru.gxfin.gate.quik.provider.QuikProviderLifeController;
import ru.gxfin.gate.quik.provider.QuikProviderSettingsController;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @SneakyThrows
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
    public ProviderLifeController providerLifeController() {
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
    public QuikProviderSecuritiesDataController quikProviderSecuritiesDataController() {
        return new QuikProviderSecuritiesDataController();
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
        result.add(quikProviderSecuritiesDataController());
        result.add(quikProviderSessionStateDataController());
        return result;
    }
}
