package ru.gx.fin.gate.quik.datacontrollers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.gx.core.simpleworker.SimpleWorkerOnIterationExecuteEvent;
import ru.gx.fin.gate.quik.connector.QuikConnector;
import ru.gx.fin.gate.quik.errors.QuikConnectorException;
import ru.gx.fin.gate.quik.events.QuikProviderDoCleanEvent;
import ru.gx.fin.gate.quik.provider.QuikProviderSettingsContainer;
import ru.gx.fin.gate.quik.provider.out.QuikSessionState;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static lombok.AccessLevel.PROTECTED;

/**
 * Контролер чтения состояния сессии
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class QuikProviderSessionStateDataController implements ProviderDataController {
    @Getter(PROTECTED)
    @NotNull
    private final QuikProviderSettingsContainer settings;

    @Getter(PROTECTED)
    @NotNull
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Ссылка на коннектор, получаем из провайдера
     */
    @Getter(PROTECTED)
    @NotNull
    private final QuikConnector connector;

    @Getter
    @Setter(PROTECTED)
    private QuikSessionState lastSessionState;

    @Getter
    @Setter(PROTECTED)
    private long lastReadSessionStateMs;

    @Override
    public void load(SimpleWorkerOnIterationExecuteEvent iterationExecuteEvent) throws IOException, QuikConnectorException {
        if (!needReload()) {
            return;
        }

        final var originalSessionState = this.connector.getSessionState();
        setLastReadSessionStateMs(System.currentTimeMillis());

        final var newSessionState = new QuikSessionState(
                originalSessionState.isConnected(),
                originalSessionState.getSessionId(),
                originalSessionState.getServerTime(),
                originalSessionState.getConnectionTime(),
                originalSessionState.getVersion(),
                originalSessionState.getConnection(),
                originalSessionState.getIpAddress(),
                originalSessionState.getIpPort(),
                originalSessionState.getIpComment()
        );

        if (newSessionState.isConnected() && this.lastSessionState != null
                && !newSessionState.getSessionId().equals(this.lastSessionState.getSessionId())) {
            this.eventPublisher.publishEvent(new QuikProviderDoCleanEvent(this));
        }

        setLastSessionState(newSessionState);
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
