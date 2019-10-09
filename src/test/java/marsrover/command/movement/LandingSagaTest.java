package marsrover.command.movement;

import marsrover.command.Coordinates;

import marsrover.command.rover.MarsRoverId;

import marsrover.command.surface.SurfaceId;

import marsrover.coreapi.rover.ObjectPlacedEvent;
import marsrover.coreapi.rover.PlaceObjectCommand;
import marsrover.coreapi.rover.RoverLandedEvent;

import org.axonframework.test.saga.FixtureConfiguration;
import org.axonframework.test.saga.SagaTestFixture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static marsrover.command.Direction.NORTH;


class LandingSagaTest {

    private FixtureConfiguration fixtureConfiguration;

    @BeforeEach
    void before() {

        fixtureConfiguration = new SagaTestFixture<>(LandingSaga.class);
    }


    @Test
    void roverLandedPlaceObject() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        Coordinates target = new Coordinates(2, 3);

        RoverLandedEvent roverLandedEvent = new RoverLandedEvent(roverId, surfaceId, NORTH, target);
        PlaceObjectCommand placeObjectCommand = new PlaceObjectCommand(surfaceId, roverId, target);

        fixtureConfiguration
            .givenAggregate(roverId.toString()).published()
            .whenAggregate(roverId.toString()).publishes(roverLandedEvent)
            .expectAssociationWith("objectId", roverId.toString())
            .expectDispatchedCommands(placeObjectCommand)
            .expectActiveSagas(1);
    }


    @Test
    void objectPlaced() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        Coordinates target = new Coordinates(2, 3);

        RoverLandedEvent roverLandedEvent = new RoverLandedEvent(roverId, surfaceId, NORTH, target);
        ObjectPlacedEvent objectPlacedEvent = new ObjectPlacedEvent(surfaceId, roverId, target);

        fixtureConfiguration
            .givenAggregate(roverId.toString()).published(roverLandedEvent)
            .whenAggregate(surfaceId.toString()).publishes(objectPlacedEvent)
            .expectNoDispatchedCommands()
            .expectActiveSagas(0);
    }
}
