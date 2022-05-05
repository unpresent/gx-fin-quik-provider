package ru.gx.fin.gate.quik.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gx.fin.gate.quik.datacontrollers.*;

import java.util.ArrayList;
import java.util.List;

@EnableConfigurationProperties({ConfigurationPropertiesQuik.class})
@Configuration
public class CommonConfig {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="DataControllers">
    @Bean
    public List<ProviderDataController> providerDataControllers(
            @NotNull final QuikProviderAllTradesDataController quikProviderAllTradesDataController,
            @NotNull final QuikProviderDealsDataController quikProviderDealsDataController,
            @NotNull final QuikProviderOrdersDataController quikProviderOrdersDataController,
            @NotNull final QuikProviderSecuritiesDataController quikProviderSecuritiesDataController,
            @NotNull final QuikProviderSessionStateDataController quikProviderSessionStateDataController
    ) {
        final var result = new ArrayList<ProviderDataController>();
        result.add(quikProviderAllTradesDataController);
        result.add(quikProviderDealsDataController);
        result.add(quikProviderOrdersDataController);
        result.add(quikProviderSecuritiesDataController);
        result.add(quikProviderSessionStateDataController);
        return result;
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
