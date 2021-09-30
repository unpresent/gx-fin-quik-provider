package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.model.internal.QuikSessionState;
import ru.gx.fin.gate.quik.provider.QuikProvider;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.worker.SimpleIterationExecuteEvent;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static lombok.AccessLevel.*;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
public class QuikProviderSessionStateDataController implements ProviderDataController {
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

    /**
     * Ссылка на сам Провайдер, получаем в конструкторе
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProvider provider;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikConnector connector;

    @Getter
    @Setter(PROTECTED)
    private QuikSessionState lastSessionState;

    @Getter
    @Setter(PROTECTED)
    private long lastReadSessionStateMs;

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
    public void load(SimpleIterationExecuteEvent iterationExecuteEvent) throws IOException, QuikConnectorException {
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
