package ru.gagarkin.gxfin.quik.provider;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import ru.gagarkin.gxfin.gate.quik.connector.QuikConnector;
import ru.gagarkin.gxfin.quik.api.ProviderDataController;
import ru.gagarkin.gxfin.quik.events.ProviderReadSessionStateEvent;
import ru.gagarkin.gxfin.quik.events.ProviderSettingsChangedEvent;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class QuikProviderRunner implements Runnable {
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Settings">
    private int attemptsOnConnect;
    private int pauseMsOnConnect;
    private int pauseMsOnReload;
    private int intervalMsMandatoryReadState;

    private void resetSettings() {
        log.info("resetSettings()");
        this.attemptsOnConnect = this.settings.getAttemptsOnConnect();
        log.info("this.attemptsOnConnect = ", this.attemptsOnConnect);
        this.pauseMsOnConnect = this.settings.getPauseMsOnConnect();
        log.info("this.pauseMsOnConnect = ", this.pauseMsOnConnect);
        this.pauseMsOnReload = this.settings.getPauseMsOnReload();
        log.info("this.pauseMsOnReload = ", this.pauseMsOnReload);
        this.intervalMsMandatoryReadState = this.settings.getIntervalMsMandatoryReadState();
        log.info("this.intervalMsMandatoryReadState = ", this.intervalMsMandatoryReadState);
    }

    @EventListener
    public void onEventChangedSettings(ProviderSettingsChangedEvent event) {
        log.info("onEventChangedSettings({})", event.getSettingName());
        switch (event.getSettingName()) {
            case QuikProviderSettings.ATTEMPTS_ON_CONNECT:
                this.attemptsOnConnect = this.settings.getAttemptsOnConnect();
                log.info("this.attemptsOnConnect = ", this.attemptsOnConnect);
                break;
            case QuikProviderSettings.PAUSE_MS_ON_CONNECT:
                this.pauseMsOnConnect = this.settings.getPauseMsOnConnect();
                log.info("this.pauseMsOnConnect = ", this.pauseMsOnConnect);
                break;
            case QuikProviderSettings.PAUSE_MS_ON_RELOAD:
                this.pauseMsOnReload = this.settings.getPauseMsOnReload();
                log.info("this.pauseMsOnReload = ", this.pauseMsOnReload);
                break;
            case QuikProviderSettings.INTERVAL_MS_MANDATORY_READ_STATE:
                this.intervalMsMandatoryReadState = this.settings.getIntervalMsMandatoryReadState();
                log.info("this.intervalMsMandatoryReadState = ", this.intervalMsMandatoryReadState);
                break;
            case QuikProviderSettings.ALL:
                resetSettings();
                break;
            default:
                break;
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Fields">
    private final ApplicationContext context;

    @Getter
    private final QuikConnector connector;

    private final QuikProviderSettings settings;

    private final List<ProviderDataController> dataControllers;

    @Getter
    private volatile Thread worker;

    @Getter
    @Setter
    private volatile boolean isRunning;

    @Getter
    private volatile boolean needRestart;

    private final ProviderReadSessionStateEvent readSessionStateEvent;
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Initialization">
    public QuikProviderRunner(ApplicationContext context, QuikConnector connector, QuikProviderSettings settings) {
        this.context = context;
        this.connector = connector;
        this.settings = settings;
        this.dataControllers = new ArrayList<>();
        this.resetSettings();
        this.readSessionStateEvent = new ProviderReadSessionStateEvent(this);
    }

    public void registerDataController(ProviderDataController controller) {
        if (this.dataControllers.indexOf(controller) < 0) {
            this.dataControllers.add(controller);
        }
    }

    public void unRegisterDataController(ProviderDataController controller) {
        this.dataControllers.remove(controller);
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
    // <editor-fold desc="Main functional">
    @Override
    public void run() {
        log.info("Starting run()");
        this.needRestart = false;
        this.isRunning = true;
        this.worker = Thread.currentThread();
        try {
            while (this.isRunning) {
                try {
                    if (!internalCheckConnected())
                        continue;

                    if (!this.needImmediateLoad()) {
                        internalReadState();
                    } else {
                        internalReadData();
                    }

                    if (!this.needImmediateLoad()) {
                        Thread.sleep(this.pauseMsOnReload);
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    log.error(e.getStackTrace().toString());
                    this.isRunning = false;
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage());
                    log.error(e.getStackTrace().toString());
                    this.needRestart = true;
                }
            }
        } finally {
            this.worker = null;
            log.info("Finished run()");
        }
    }

    public void stop() {
        this.needRestart = false;
        this.isRunning = false;
    }

    protected boolean needImmediateLoad() {
        for (var i = 0; i < this.dataControllers.size(); i++) {
            if (this.dataControllers.get(i).needReload())
                return true;
        }
        return false;
    }

    protected boolean internalCheckConnected() {
        if (!this.connector.isActive()) {
            try {
                for (var i = 0; i < this.attemptsOnConnect; i++) {
                    if (!this.isRunning) {
                        return false;
                    }

                    if (this.connector.tryConnect()) {
                        log.info("Provider connected!");
                        return true;
                    }

                    Thread.sleep(this.pauseMsOnConnect);
                }
                return false;
            } catch (Exception e) {
                internalTreatmentExceptionOnDataRead(e);
                return false;
            }
        }
        return true;
    }

    protected void internalReadState() {
        this.context.publishEvent(this.readSessionStateEvent);
    }

    protected void internalReadData() {
        for (var i = 0; i < this.dataControllers.size(); i++) {
            var dataController = dataControllers.get(i);
            if (dataController.needReload()) {
                var e = dataController.createEvent(this);
                this.context.publishEvent(e);
            }
        }
    }

    private void internalTreatmentExceptionOnDataRead(Exception e) {
        if (e instanceof InterruptedException) {
            this.isRunning = false;
        }
        log.error(e.getMessage());
        log.error(e.getStackTrace().toString());
        try {
            this.connector.disconnect();
        } catch (Exception e2) {
            log.error(e2.getMessage());
            log.error(e2.getStackTrace().toString());
        }
    }
    // </editor-fold>
    // -----------------------------------------------------------------------------------------------------------------
}
