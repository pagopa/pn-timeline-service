package it.pagopa.pn.timelineservice;

import it.pagopa.pn.commons.configs.listeners.TaskIdApplicationListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimelineServiceApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TimelineServiceApplication.class);
        app.addListeners(new TaskIdApplicationListener());
        app.run(args);
    }
}