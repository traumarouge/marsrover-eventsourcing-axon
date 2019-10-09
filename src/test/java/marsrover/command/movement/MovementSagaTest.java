package marsrover.command.movement;

import marsrover.command.Coordinates;
import marsrover.command.Identifier;

import marsrover.command.rover.MarsRoverId;

import marsrover.command.surface.SurfaceId;

import marsrover.coreapi.rover.CollisionOnRoverCommand;
import marsrover.coreapi.rover.MoveObjectCommand;
import marsrover.coreapi.rover.ObjectMovedEvent;
import marsrover.coreapi.rover.ObjectsCollidedEvent;
import marsrover.coreapi.rover.RoverMovementStartedEvent;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static marsrover.command.Direction.NORTH;


class MovementSagaTest {

    private FixtureConfiguration fixtureConfiguration;

    @BeforeEach
    void before() {

        fixtureConfiguration = new SagaTestFixture<>(MovementSaga.class);
    }


    @Test
    void roverMovedMoveObject() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        String movementId = UUID.randomUUID().toString();

        RoverMovementStartedEvent roverMovementStartedEvent = new RoverMovementStartedEvent(roverId, surfaceId, NORTH,
                movementId);

        MoveObjectCommand moveObjectCommand = new MoveObjectCommand(surfaceId, roverId, NORTH, movementId);

        fixtureConfiguration
            .givenAggregate(roverId.toString()).published()
            .whenAggregate(roverId.toString()).publishes(roverMovementStartedEvent)
            .expectDispatchedCommands(moveObjectCommand)
            .expectActiveSagas(1);
    }


    @Test
    void objectMoved() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        String movementId = UUID.randomUUID().toString();

        RoverMovementStartedEvent roverMovementStartedEvent = new RoverMovementStartedEvent(roverId, surfaceId, NORTH,
                movementId);

        Coordinates source = new Coordinates(2, 3);
        Coordinates target = new Coordinates(2, 4);

        ObjectMovedEvent objectMovedEvent = new ObjectMovedEvent(surfaceId, roverId, movementId, source, target);

        fixtureConfiguration
            .givenAggregate(roverId.toString()).published(roverMovementStartedEvent)
            .whenAggregate(surfaceId.toString()).publishes(objectMovedEvent)
            .expectNoDispatchedCommands()
            .expectActiveSagas(0);
    }


    @Test
    void objectsCollidedCollisionOnMovingRoverOnly() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        Identifier objectId = new Identifier(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        String movementId = UUID.randomUUID().toString();

        RoverMovementStartedEvent roverMovementStartedEvent = new RoverMovementStartedEvent(roverId, surfaceId, NORTH,
                movementId);

        ObjectsCollidedEvent objectsCollidedEvent = new ObjectsCollidedEvent(surfaceId, roverId, movementId, roverId,
                objectId);

        CollisionOnRoverCommand collisionOnMovingRoverCommand = new CollisionOnRoverCommand(roverId, true);

        fixtureConfiguration
            .givenAggregate(roverId.toString()).published(roverMovementStartedEvent)
            .whenAggregate(surfaceId.toString()).publishes(objectsCollidedEvent)
            .expectDispatchedCommands(collisionOnMovingRoverCommand)
            .expectActiveSagas(0);
    }


    @Test
    void objectsCollidedCollisionOnMovingRoverAndPassiveRover() {

        MarsRoverId roverOneId = new MarsRoverId(UUID.randomUUID().toString());
        MarsRoverId roverTwoId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        String movementId = UUID.randomUUID().toString();

        RoverMovementStartedEvent roverMovementStartedEvent = new RoverMovementStartedEvent(roverOneId, surfaceId,
                NORTH, movementId);

        ObjectsCollidedEvent objectCollidedEvent = new ObjectsCollidedEvent(surfaceId, roverOneId, movementId,
                roverOneId, roverTwoId);

        CollisionOnRoverCommand collisionOnMovingRoverCommand = new CollisionOnRoverCommand(roverOneId, true);
        CollisionOnRoverCommand collisionOnPassiveRoverCommand = new CollisionOnRoverCommand(roverTwoId, false);

        fixtureConfiguration
            .givenAggregate(roverOneId.toString()).published(roverMovementStartedEvent)
            .whenAggregate(surfaceId.toString()).publishes(objectCollidedEvent)
            .expectDispatchedCommands(collisionOnMovingRoverCommand, collisionOnPassiveRoverCommand)
            .expectActiveSagas(0);
    }
}
