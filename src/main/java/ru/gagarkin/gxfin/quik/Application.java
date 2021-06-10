package ru.gagarkin.gxfin.quik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.gagarkin.gxfin.quik.events.ProviderStartEvent;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        var context = SpringApplication.run(Application.class, args);
        context.publishEvent(new ProviderStartEvent("Application"));
    }

}
