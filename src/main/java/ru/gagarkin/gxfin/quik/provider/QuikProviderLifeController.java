package ru.gagarkin.gxfin.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDemonController;
import ru.gagarkin.gxfin.quik.api.ProviderLifeController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.*;

@Slf4j
public class QuikProviderLifeController implements ProviderLifeController {
    private final Provider provider;
    private final ProviderDemonController timeoutsController;

    @Autowired
    public QuikProviderLifeController(QuikProvider provider, ProviderDemonController timeoutsController) {
        this.provider = provider;
        this.timeoutsController = timeoutsController;
    }

    @EventListener
    @Override
    public void onEvent(ProviderStartEvent event) {
        log.info("Starting onEvent(ProviderStartEvent event)");
        this.provider.stop();
        this.provider.start();

        if (!this.timeoutsController.isActive()) {
            log.info("starting timeoutsController!");
            new Thread(this.timeoutsController).start();
        }
        log.info("Finished onEvent(ProviderStartEvent event)");
    }

    @EventListener
    @Override
    public void onEvent(ProviderStopEvent event) {
        log.info("Starting onEvent(ProviderStopEvent event)");
        this.provider.stop();
        log.info("Finished onEvent(ProviderStopEvent event)");
    }

    @EventListener
    @Override
    public void onEvent(ProviderStartWithCleanEvent event) {
        log.info("Starting onEvent(ProviderStartWithCleanEvent event)");
        this.provider.stop();
        try {
            this.provider.clean();
        } catch (ProviderException e) {
            log.error(e.getMessage());
            log.error(e.getStackTrace().toString());
        }
        this.provider.start();
        log.info("Finished onEvent(ProviderStartWithCleanEvent event)");
    }

    @EventListener
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

    @EventListener
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
