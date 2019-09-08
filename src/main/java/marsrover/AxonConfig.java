package marsrover;

import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;

import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.inmemory.InMemorySagaStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AxonConfig {

    @Bean
    public EventStorageEngine storageEngine() {

        return new InMemoryEventStorageEngine();
    }


    @Bean
    public SagaStore sagaStore() {

        return new InMemorySagaStore();
    }
}
