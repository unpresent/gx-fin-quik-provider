package ru.gx.fin.gate.quik.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "quik")
@Getter
@Setter
public class ConfigurationPropertiesQuik {
    public static final String ATTEMPTS_ON_CONNECT = "quik.attempts-on-connect";
    public static final String PAUSE_ON_CONNECT_MS = "quik.pause-on-connect-ms";
    public static final String INTERVAL_MANDATORY_READ_STATE_MS = "quik.interval-mandatory-read-state-ms";

    private int attemptsOnConnect = 20;
    private int pauseOnConnectMs = 3000;
    private int intervalMandatoryReadStateMs = 5000;
}
