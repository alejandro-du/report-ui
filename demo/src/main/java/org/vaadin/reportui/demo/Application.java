package org.vaadin.reportui.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static final Logger log = LoggerFactory.getLogger(Application.class);

    @Bean
    public ApplicationListener<ContextRefreshedEvent> initDatabase(CallRepository callRepository) {
        return event -> {
            if (callRepository.count() == 0) {
                createDemoData(callRepository);
            }
        };
    }

    private void createDemoData(CallRepository callRepository) {
        log.info("Creating demo data...");

        String[] firstNames = {"John", "Peter", "Alice", "Joshua", "Mike", "Olivia", "Nina", "Alex", "Rita", "Dan", "Umberto", "Henrik", "Marian", "Edgar", "Juan"};
        String[] lastNames = {"Smith", "Gordon", "Simpson", "Brown", "Clavel", "Simons", "Verne", "Scott", "Allison", "Gates", "Rowling", "Barks", "Ross", "Schneider", "Duarte"};

        Random r = new Random(0);

        for (int i = 0; i < 100; i++) {
            Call call = new Call();
            call.setClient(firstNames[r.nextInt(firstNames.length)] + " " + lastNames[r.nextInt(lastNames.length)]);
            call.setPhoneNumber("555 01" + r.nextInt(10) + " " + r.nextInt(10) + r.nextInt(10) + r.nextInt(10));
            call.setCity(City.values()[r.nextInt(City.values().length)]);
            call.setStartTime(LocalDateTime.now().minusDays(r.nextInt(15)).minusHours(r.nextInt(23)).minusMinutes(r.nextInt(59)).minusSeconds(59));
            call.setDuration(r.nextInt(30 * 60));
            call.setStatus(Status.values()[r.nextInt(Status.values().length)]);
            callRepository.save(call);
        }

        log.info("Demo data created.");
    }

}
