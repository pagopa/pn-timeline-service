package it.pagopa.pn.timelineservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class TimelineServiceApplication {


    public static void main(String[] args) {
        SpringApplication.run(TimelineServiceApplication.class, args);
    }


    @RestController
    @RequestMapping("/")
    public static class RootController {

        @GetMapping("/")
        public String home() {
            return "";
        }
    }
}