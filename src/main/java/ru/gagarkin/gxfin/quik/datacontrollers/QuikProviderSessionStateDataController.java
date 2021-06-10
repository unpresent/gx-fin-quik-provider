package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderReadSessionStateEvent;

import java.io.IOException;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
@Component
public class QuikProviderSessionStateDataController {
    private final Provider provider;
    private final QuikConnector connector;

    @Autowired
    public QuikProviderSessionStateDataController(Provider provider) {
        super();
        this.provider = provider;
        this.connector = provider.getConnector();
    }

    @EventListener(ProviderReadSessionStateEvent.class)
    public void onEvent(ProviderReadSessionStateEvent event) throws IOException, QuikConnectorException, ProviderException {
        this.provider.registerStartExecution();
        try {
            var sessionState = this.connector.getSessionState();
            event.setLastReadedSessionStateMs(System.currentTimeMillis());
            event.setLastSessionState(sessionState);
            log.info("Loaded sessionState (isConnected = {}, serverTime = {})", sessionState.isConnected, sessionState.serverTime);
        } finally {
            this.provider.registerFinishExecution();
        }
    }
}
