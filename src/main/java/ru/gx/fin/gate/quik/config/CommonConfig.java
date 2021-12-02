package ru.gx.fin.gate.quik.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import ru.gx.core.settings.StandardSettingsController;
import ru.gx.fin.gate.quik.converters.QuikAllTradeFromOriginalQuikAllTradeConverter;
import ru.gx.fin.gate.quik.converters.QuikDealFromOriginalQuikDealConverter;
import ru.gx.fin.gate.quik.converters.QuikOrderFromOriginalQuikOrderConverter;
import ru.gx.fin.gate.quik.converters.QuikSecurityFromOriginalQuikSecurityConverter;
import ru.gx.fin.gate.quik.datacontrollers.*;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@EnableConfigurationProperties({ConfigurationPropertiesServiceKafka.class, ConfigurationPropertiesQuik.class})
public abstract class CommonConfig {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Common">
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Provider & Settings">
    // TODO: Должно создаваться в gx-fin-quik-gateway.
    @Bean
    @Autowired
    public QuikProviderSettingsContainer quikProviderSettingsController(@NotNull final StandardSettingsController standardSettingsController) {
        return new QuikProviderSettingsContainer(standardSettingsController);
    }

    @Bean
    public QuikProvider provider() {
        return new QuikProvider();
    }

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Converters">
    @Bean
    public QuikAllTradeFromOriginalQuikAllTradeConverter quikAllTradeFromOriginalQuikAllTradeConverter() {
        return new QuikAllTradeFromOriginalQuikAllTradeConverter();
    }

    @Bean
    public QuikOrderFromOriginalQuikOrderConverter quikOrderFromOriginalQuikOrderConverter() {
        return new QuikOrderFromOriginalQuikOrderConverter();
    }

    @Bean
    public QuikDealFromOriginalQuikDealConverter quikDealFromOriginalQuikDealConverter() {
        return new QuikDealFromOriginalQuikDealConverter();
    }

    @Bean
    public QuikSecurityFromOriginalQuikSecurityConverter quikSecurityFromOriginalQuikSecurityConverter() {
        return new QuikSecurityFromOriginalQuikSecurityConverter();
    }    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="DataControllers">
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

    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Kafka Producer">
    @Value(value = "${service.kafka.server}")
    private String kafkaServer;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        final var configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public ChannelsConfiguratorImpl quikChannelsConfigurator() {
        return new ChannelsConfiguratorImpl();
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
