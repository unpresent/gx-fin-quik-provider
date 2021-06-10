package ru.gagarkin.gxfin.quik.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.gagarkin.gxfin.quik.events.ProviderSettingsChangedEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class QuikProviderSettings {
    public static final String ALL = "*";
    public static final String QUIK_PIPE_NAME = "quik_pipe_name";
    public static final String BUFFER_SIZE = "buffer_size";
    public static final String ATTEMPTS_ON_CONNECT = "attempts_on_connect";
    public static final String PAUSE_MS_ON_CONNECT = "pause_ms_on_connect";
    public static final String PAUSE_MS_ON_RELOAD = "pause_ms_on_reload";
    public static final String INTERVAL_MS_MANDATORY_READ_STATE = "interval_ms_mandatory_read_state";
    public static final String TIMEOUT_DATA_READ = "timeout_data_read";

    private final ApplicationContext context;

    private final Map<String, Object> settings;

    @Autowired
    public QuikProviderSettings(ApplicationContext context) {
        this.context = context;
        this.settings = new HashMap<>();

        // TODO: Переписать на чтение настроек
        setSetting(QUIK_PIPE_NAME, "C-QUIK_VTB-");
        setSetting(BUFFER_SIZE, 32 * 1024);
        setSetting(ATTEMPTS_ON_CONNECT, 10);
        setSetting(PAUSE_MS_ON_CONNECT, 3000);
        setSetting(PAUSE_MS_ON_RELOAD, 500);
        setSetting(INTERVAL_MS_MANDATORY_READ_STATE, 5000);
        setSetting(TIMEOUT_DATA_READ, 10000);
    }

    public Object getSetting(String settingName) {
        return this.settings.get(settingName);
    }

    public void setSetting(String settingName, Object value) {
        var oldValue = this.settings.get(settingName);
        if ((oldValue == null && value != null) || (!oldValue.equals(value))) {
            log.info("setSetting({}, {})", settingName, value);
            this.settings.put(settingName, value);
            log.info("publishEvent(ProviderSettingsChangedEvent({}))", settingName);
            context.publishEvent(new ProviderSettingsChangedEvent(this, settingName));
        }
    }

    public String getQuikPipeName() {
        return (String)this.getSetting(QUIK_PIPE_NAME);
    }

    public int getBufferSize() {
        return (Integer)this.getSetting(BUFFER_SIZE);
    }

    public int getAttemptsOnConnect() {
        return (Integer)this.getSetting(ATTEMPTS_ON_CONNECT);
    }

    public int getPauseMsOnConnect() {
        return (Integer)this.getSetting(PAUSE_MS_ON_CONNECT);
    }

    public int getPauseMsOnReload() {
        return (Integer)this.getSetting(PAUSE_MS_ON_RELOAD);
    }

    public int getIntervalMsMandatoryReadState() {
        return (Integer)this.getSetting(INTERVAL_MS_MANDATORY_READ_STATE);
    }

    public int getTimeoutDataRead() {
        return (Integer)this.getSetting(TIMEOUT_DATA_READ);
    }
}
