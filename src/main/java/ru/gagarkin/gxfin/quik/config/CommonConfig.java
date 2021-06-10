package ru.gagarkin.gxfin.quik.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import ru.gagarkin.gxfin.quik.provider.QuikProvider;
import ru.gagarkin.gxfin.quik.provider.QuikProviderDemonController;
import ru.gagarkin.gxfin.quik.provider.QuikProviderLifeController;
import ru.gagarkin.gxfin.quik.provider.QuikProviderSettings;

public class CommonConfig {
    @Bean
    @Autowired
    public QuikProviderSettings quikProviderSettings(ApplicationContext context) {
        return new QuikProviderSettings(context);
    }

    @Bean
    @Autowired
    public QuikProvider quikProvider(ApplicationContext context, QuikProviderSettings settings) {
        return new QuikProvider(context, settings);
    }

    @Bean
    @Autowired
    public QuikProviderDemonController quikProviderDemonController(QuikProvider provider, QuikProviderSettings settings) {
        return new QuikProviderDemonController(provider, settings);
    }

    @Bean
    @Autowired
    public QuikProviderLifeController quikProviderLifeController(QuikProvider provider, QuikProviderDemonController demonController) {
        return new QuikProviderLifeController(provider, demonController);
    }
}
