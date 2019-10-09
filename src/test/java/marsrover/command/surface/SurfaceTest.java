package marsrover.command.surface;

import marsrover.command.Coordinates;
import marsrover.command.Identifier;

import marsrover.coreapi.rover.CreateSurfaceCommand;
import marsrover.coreapi.rover.MoveObjectCommand;
import marsrover.coreapi.rover.ObjectMovedEvent;
import marsrover.coreapi.rover.ObjectPlacedEvent;
import marsrover.coreapi.rover.ObjectsCollidedEvent;
import marsrover.coreapi.rover.PlaceObjectCommand;
import marsrover.coreapi.rover.SurfaceCreatedEvent;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;

import org.hamcrest.Matcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static marsrover.command.Direction.NORTH;

import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.axonframework.test.matchers.Matchers.messageWithPayload;
import static org.axonframework.test.matchers.Matchers.payloadsMatching;
import static org.axonframework.test.matchers.Matchers.predicate;

import static org.hamcrest.Matchers.instanceOf;


class SurfaceTest {

    private FixtureConfiguration<Surface> fixtureConfiguration;

    @BeforeEach
    void before() {

        fixtureConfiguration = new AggregateTestFixture<>(Surface.class);
    }


    @Test
    void createSurface() {

        Dimension dim = new Dimension(10, 10);
        Matcher<SurfaceCreatedEvent> matcher = predicate(e -> e.getDimension().equals(dim));

        CreateSurfaceCommand command = new CreateSurfaceCommand(dim);

        fixtureConfiguration
            .given()
            .when(command)
            .expectResultMessageMatching(messageWithPayload(instanceOf(SurfaceId.class)))
            .expectEventsMatching(payloadsMatching(exactSequenceOf(matcher, andNoMore())));
    }


    @Test
    void placeObject() {

        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        SurfaceCreatedEvent createdEvent = new SurfaceCreatedEvent(surfaceId, new Dimension(10, 10));

        Identifier objectId = new Identifier(UUID.randomUUID().toString());
        Coordinates target = new Coordinates(2, 3);

        PlaceObjectCommand placeObjectCommand = new PlaceObjectCommand(surfaceId, objectId, target);
        ObjectPlacedEvent objectPlacedEvent = new ObjectPlacedEvent(surfaceId, objectId, target);

        fixtureConfiguration
            .given(createdEvent)
            .when(placeObjectCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(objectPlacedEvent);
    }


    @Test
    void moveObject() {

        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        SurfaceCreatedEvent createdEvent = new SurfaceCreatedEvent(surfaceId, new Dimension(10, 10));

        Identifier objectId = new Identifier(UUID.randomUUID().toString());
        Coordinates source = new Coordinates(2, 3);
        Coordinates target = new Coordinates(2, 4);

        ObjectPlacedEvent objectPlacedEvent = new ObjectPlacedEvent(surfaceId, objectId, source);

        String movementId = UUID.randomUUID().toString();
        MoveObjectCommand moveObjectCommand = new MoveObjectCommand(surfaceId, objectId, NORTH, movementId);
        ObjectMovedEvent objectMovedEvent = new ObjectMovedEvent(surfaceId, objectId, movementId, source, target);

        fixtureConfiguration
            .given(createdEvent, objectPlacedEvent)
            .when(moveObjectCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(objectMovedEvent);
    }


    @Test
    void collideObject() {

        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        SurfaceCreatedEvent createdEvent = new SurfaceCreatedEvent(surfaceId, new Dimension(10, 10));

        Identifier objectOneId = new Identifier(UUID.randomUUID().toString());
        Identifier objectTwoId = new Identifier(UUID.randomUUID().toString());
        Coordinates source = new Coordinates(2, 3);
        Coordinates target = new Coordinates(2, 4);

        ObjectPlacedEvent objectOnePlacedEvent = new ObjectPlacedEvent(surfaceId, objectOneId, source);
        ObjectPlacedEvent objectTwoPlacedEvent = new ObjectPlacedEvent(surfaceId, objectTwoId, target);

        String movementId = UUID.randomUUID().toString();
        MoveObjectCommand moveObjectOneCommand = new MoveObjectCommand(surfaceId, objectOneId, NORTH, movementId);

        ObjectsCollidedEvent objectsCollidedEvent = new ObjectsCollidedEvent(surfaceId, objectOneId, movementId,
                objectOneId, objectTwoId);

        fixtureConfiguration
            .given(createdEvent, objectOnePlacedEvent, objectTwoPlacedEvent)
            .when(moveObjectOneCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(objectsCollidedEvent);
    }
}
