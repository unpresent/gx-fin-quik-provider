package ru.gagarkin.gxfin.quik.provider;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.gagarkin.gxfin.quik.api.ProviderDemonController;
import ru.gagarkin.gxfin.quik.events.ProviderSettingsChangedEvent;

@Slf4j
public class QuikProviderDemonController implements ProviderDemonController {
    private int timeoutDataRead;
    private int pauseMsBetweenChecks;

    private final QuikProviderSettings settings;
    private final QuikProvider provider;

    @Getter
    private boolean isActive;

    @Autowired
    public QuikProviderDemonController(QuikProvider provider, QuikProviderSettings settings) {
        this.provider = provider;
        this.settings = settings;
        resetSettings();
    }

    private void resetSettings() {
        this.timeoutDataRead = settings.getTimeoutDataRead();
        this.pauseMsBetweenChecks = this.timeoutDataRead / 4;
    }

    @EventListener
    public void onEventChangedSettings(ProviderSettingsChangedEvent event) {
        switch (event.getSettingName()) {
            case QuikProviderSettings.TIMEOUT_DATA_READ:
            case QuikProviderSettings.ALL:
                resetSettings();
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        log.info("Starting run()");
        try {
            while (true) {
                this.isActive = true;

                try {
                    Thread.sleep(pauseMsBetweenChecks);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    log.error(e.getStackTrace().toString());
                }

                if (this.provider.getPassedSinceLastStart() > this.timeoutDataRead) {
                    log.info("Timeout data reading! Init restart");
                    this.provider.stop();
                    this.provider.start();
                }

                if (this.provider.needRestart()) {
                    log.info("Need restarting! Init restart");
                    this.provider.stop();
                    this.provider.start();
                }
            }
        } finally {
            log.info("Finished run()");
        }
    }
}
