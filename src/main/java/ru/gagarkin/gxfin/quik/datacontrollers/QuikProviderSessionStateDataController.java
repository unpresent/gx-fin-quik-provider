package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.gate.quik.data.internal.SessionState;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.api.ProviderSettingsController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderIterationExecuteEvent;
import ru.gagarkin.gxfin.quik.provider.QuikProviderSettingsController;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
public class QuikProviderSessionStateDataController implements ProviderDataController {
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private ProviderSettingsController settings;

    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private Provider provider;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikConnector connector;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private SessionState lastSessionState;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private long lastReadedSessionStateMs;

    @Autowired
    public QuikProviderSessionStateDataController() {
        super();
    }

    @PostConstruct
    public void postInit() {
        if (this.provider == null) {
            log.error("this.provider == null");
        }
        if (this.connector == null) {
            log.error("this.connector == null");
        }
    }

    @Override
    public void load(ProviderIterationExecuteEvent iterationExecuteEvent) throws ProviderException, IOException, QuikConnectorException {
        if (!needReload()) {
            return;
        }

        final var quikSessionState = this.connector.getSessionState();
        setLastReadedSessionStateMs(System.currentTimeMillis());
        setLastSessionState(new SessionState(quikSessionState));
        log.info("Loaded sessionState (isConnected = {}, serverTime = {})", quikSessionState.isConnected, quikSessionState.serverTime);
    }


    /**
     * Вычисляется необходимость прям сейчас чтения состояния.
     * @return true - надо прочитать данные прям сейчас
     */
    public boolean needReload() {
        final var now = System.currentTimeMillis();
        return (now - this.getLastReadedSessionStateMs() > this.settings.getIntervalMandatoryReadStateMs());
    }

    @Override
    public void clean() {
    }
}
