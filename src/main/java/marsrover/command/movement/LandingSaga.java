package marsrover.command.movement;

import marsrover.command.rover.MarsRoverId;

import marsrover.coreapi.rover.ObjectPlacedEvent;
import marsrover.coreapi.rover.PlaceObjectCommand;
import marsrover.coreapi.rover.RoverLandedEvent;

import org.axonframework.commandhandling.gateway.CommandGateway;

import org.axonframework.config.ProcessingGroup;

import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;

import org.axonframework.spring.stereotype.Saga;

import org.springframework.beans.factory.annotation.Autowired;


@Saga
@ProcessingGroup("MarsRoverEventProcessingGroup")
public class LandingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "roverId")
    public void on(RoverLandedEvent event) {

        MarsRoverId roverId = event.getRoverId();
        SagaLifecycle.associateWith("objectId", roverId.toString());

        commandGateway.send(new PlaceObjectCommand(event.getSurfaceId(), roverId, event.getTargetCoordinates()));
    }


    @EndSaga
    @SagaEventHandler(associationProperty = "objectId")
    public void on(ObjectPlacedEvent event) {
    }
}
