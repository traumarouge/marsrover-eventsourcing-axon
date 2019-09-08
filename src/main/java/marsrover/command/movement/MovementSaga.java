package marsrover.command.movement;

import marsrover.command.rover.MarsRoverId;

import marsrover.coreapi.rover.CollisionOnRoverCommand;
import marsrover.coreapi.rover.MoveObjectCommand;
import marsrover.coreapi.rover.ObjectMovedEvent;
import marsrover.coreapi.rover.ObjectsCollidedEvent;
import marsrover.coreapi.rover.RoverMovementStartedEvent;

import org.axonframework.commandhandling.gateway.CommandGateway;

import org.axonframework.config.ProcessingGroup;

import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;

import org.axonframework.spring.stereotype.Saga;

import org.springframework.beans.factory.annotation.Autowired;


@Saga
@ProcessingGroup("MarsRoverEventProcessingGroup")
public class MovementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "movementId")
    public void on(RoverMovementStartedEvent event) {

        commandGateway.send(new MoveObjectCommand(event.getSurfaceId(), event.getRoverId(), event.getDirection(),
                event.getMovementId()));
    }


    @EndSaga
    @SagaEventHandler(associationProperty = "movementId")
    public void on(ObjectsCollidedEvent event) {

        if (event.getMovingObjectId() instanceof MarsRoverId) {
            commandGateway.send(new CollisionOnRoverCommand((MarsRoverId) event.getMovingObjectId(), true));
        }

        if (event.getPassiveObjectId() instanceof MarsRoverId) {
            commandGateway.send(new CollisionOnRoverCommand((MarsRoverId) event.getPassiveObjectId(), false));
        }
    }


    @EndSaga
    @SagaEventHandler(associationProperty = "movementId")
    public void on(ObjectMovedEvent event) {
    }
}
