package ru.gxfin.gate.quik.datacontrollers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gxfin.gate.quik.connector.QuikConnector;
import ru.gxfin.gate.quik.errors.QuikConnectorException;
import ru.gxfin.gate.quik.events.ProviderIterationExecuteEvent;
import ru.gxfin.gate.quik.model.internal.QuikSessionState;
import ru.gxfin.gate.quik.provider.QuikProvider;
import ru.gxfin.gate.quik.provider.QuikProviderSettingsController;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
public class QuikProviderSessionStateDataController implements ProviderDataController {
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikProviderSettingsController settings;

    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikProvider provider;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private QuikConnector connector;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private QuikSessionState lastSessionState;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private long lastReadSessionStateMs;

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
    public void load(ProviderIterationExecuteEvent iterationExecuteEvent) throws IOException, QuikConnectorException {
        if (!needReload()) {
            return;
        }

        final var quikSessionState = this.connector.getSessionState();
        setLastReadSessionStateMs(System.currentTimeMillis());
        setLastSessionState(new QuikSessionState(quikSessionState));
        log.info("Loaded sessionState (isConnected = {}, serverTime = {})", quikSessionState.isConnected(), quikSessionState.getServerTime());
    }

    /**
     * Вычисляется необходимость прям сейчас чтения состояния.
     * @return true - надо прочитать данные прям сейчас
     */
    public boolean needReload() {
        final var now = System.currentTimeMillis();
        return (now - this.getLastReadSessionStateMs() > this.settings.getIntervalMandatoryReadStateMs());
    }

    @Override
    public void clean() {
    }
}
