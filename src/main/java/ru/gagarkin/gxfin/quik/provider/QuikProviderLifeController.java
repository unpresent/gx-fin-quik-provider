package ru.gagarkin.gxfin.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderLifeController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.*;

@Slf4j
public class QuikProviderLifeController implements ProviderLifeController {
    private final Provider provider;

    @Autowired
    public QuikProviderLifeController(Provider provider) {
        this.provider = provider;
    }

    @EventListener(ProviderStartEvent.class)
    @Override
    public void onEvent(ProviderStartEvent event) {
        log.info("Starting onEvent(ProviderStartEvent event)");
        this.provider.start();
        log.info("Finished onEvent(ProviderStartEvent event)");
    }

    @EventListener(ProviderStopEvent.class)
    @Override
    public void onEvent(ProviderStopEvent event) {
        log.info("Starting onEvent(ProviderStopEvent event)");
        this.provider.stop();
        log.info("Finished onEvent(ProviderStopEvent event)");
    }

    @EventListener(ProviderStartWithCleanEvent.class)
    @Override
    public void onEvent(ProviderStartWithCleanEvent event) {
        log.info("Starting onEvent(ProviderStartWithCleanEvent event)");
        try {
            this.provider.clean();
        } catch (ProviderException e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        this.provider.start();
        log.info("Finished onEvent(ProviderStartWithCleanEvent event)");
    }

    @EventListener(ProviderStopWithCleanEvent.class)
    @Override
    public void onEvent(ProviderStopWithCleanEvent event) {
        log.info("Starting onEvent(ProviderStopWithCleanEvent event)");
        this.provider.stop();
        try {
            this.provider.clean();
        } catch (ProviderException e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        log.info("Finished onEvent(ProviderStopWithCleanEvent event)");
    }

    @EventListener(ProviderCleanEvent.class)
    @Override
    public void onEvent(ProviderCleanEvent event) {
        log.info("Starting onEvent(ProviderCleanEvent event)");
        try {
            this.provider.clean();
        } catch (ProviderException e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        log.info("Finished onEvent(ProviderCleanEvent event)");
    }
}
