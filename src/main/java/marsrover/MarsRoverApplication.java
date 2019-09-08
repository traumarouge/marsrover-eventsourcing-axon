package marsrover;

import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class MarsRoverApplication {

    public static void main(String[] args) {

        SpringApplication.run(MarsRoverApplication.class, args);
    }


    @Bean
    public EventStorageEngine storageEngine() {

        return new InMemoryEventStorageEngine();
    }
}
