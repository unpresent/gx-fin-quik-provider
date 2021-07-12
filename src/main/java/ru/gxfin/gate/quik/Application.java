package ru.gxfin.gate.quik;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.gxfin.gate.quik.events.ProviderStartEvent;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        final var context = new SpringApplicationBuilder(Application.class)
                // .web(WebApplicationType.NONE)
                .run(args);
        context.publishEvent(new ProviderStartEvent("Application"));
    }
}
