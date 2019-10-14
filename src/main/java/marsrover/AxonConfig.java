package marsrover;

import org.axonframework.config.EventProcessingConfigurer;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;


@Configuration
public class AxonConfig {

    @Autowired
    public void configureEventProcessing(EventProcessingConfigurer eventProcessingConfigurer) {

        eventProcessingConfigurer.usingSubscribingEventProcessors();
    }
}
