package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.fin.gate.quik.provider.out.QuikSessionState;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
public class QuikProviderSessionStateDataController implements ProviderDataController {
    @Getter(PROTECTED)
    @Setter(value = PROTECTED, onMethod_ = @Autowired)
    private QuikProviderSettingsContainer settings;

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
        if (this.connector == null) {
            log.error("this.connector == null");
        }
    }

    @Override
    public void load(SimpleWorkerOnIterationExecuteEvent iterationExecuteEvent) throws IOException, QuikConnectorException {
        if (!needReload()) {
            return;
        }

        final var originalSessionState = this.connector.getSessionState();
        setLastReadSessionStateMs(System.currentTimeMillis());
        setLastSessionState(
                new QuikSessionState(
                        originalSessionState.isConnected(),
                        originalSessionState.getSessionId(),
                        originalSessionState.getServerTime(),
                        originalSessionState.getConnectionTime(),
                        originalSessionState.getVersion(),
                        originalSessionState.getConnection(),
                        originalSessionState.getIpAddress(),
                        originalSessionState.getIpPort(),
                        originalSessionState.getIpComment()
                )
        );
        log.info("Loaded sessionState (isConnected = {}, serverTime = {})", originalSessionState.isConnected(), originalSessionState.getServerTime());
    }

    /**
     * Вычисляется необходимость прям сейчас чтения состояния.
     *
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
