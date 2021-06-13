package ru.gagarkin.gxfin.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.gate.quik.dto.SessionState;
import ru.gagarkin.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gagarkin.gxfin.quik.api.Provider;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.errors.ProviderException;
import ru.gagarkin.gxfin.quik.events.ProviderIterationExecuteEvent;
import ru.gagarkin.gxfin.quik.provider.QuikProviderSettings;

import java.io.IOException;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
public class QuikProviderSessionStateDataController implements ProviderDataController {
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikProviderSettings settings;

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

    @Override
    public void load(ProviderIterationExecuteEvent iterationExecuteEvent) throws ProviderException, IOException, QuikConnectorException {
        if (!needReload()) {
            return;
        }

        var sessionState = this.connector.getSessionState();
        setLastReadedSessionStateMs(System.currentTimeMillis());
        setLastSessionState(sessionState);
        log.info("Loaded sessionState (isConnected = {}, serverTime = {})", sessionState.isConnected, sessionState.serverTime);
    }


    /**
     * Вычисляется необходимость прям сейчас чтения состояния.
     * @return true - надо прочитать данные прям сейчас
     */
    public boolean needReload() {
        var now = System.currentTimeMillis();
        return (now - this.getLastReadedSessionStateMs() > this.settings.getIntervalMsMandatoryReadState());
    }

    @Override
    public void clean() {
    }
}
