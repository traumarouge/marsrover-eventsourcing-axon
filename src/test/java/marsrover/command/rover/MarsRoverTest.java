package marsrover.command.rover;

import marsrover.command.Coordinates;

import marsrover.command.surface.SurfaceId;

import marsrover.coreapi.rover.CollisionOnRoverCommand;
import marsrover.coreapi.rover.InitRoverLandingCommand;
import marsrover.coreapi.rover.RoverDamagedOnCollisionEvent;
import marsrover.coreapi.rover.RoverLandedEvent;
import marsrover.coreapi.rover.RoverTurnedEvent;
import marsrover.coreapi.rover.TurnRoverCommand;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;

import org.hamcrest.Matcher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static marsrover.command.Direction.NORTH;

import static marsrover.command.rover.Turn.RIGHT;

import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.axonframework.test.matchers.Matchers.messageWithPayload;
import static org.axonframework.test.matchers.Matchers.payloadsMatching;
import static org.axonframework.test.matchers.Matchers.predicate;

import static org.hamcrest.Matchers.instanceOf;


class MarsRoverTest {

    private FixtureConfiguration<MarsRover> fixtureConfiguration;

    @BeforeEach
    void before() {

        fixtureConfiguration = new AggregateTestFixture<>(MarsRover.class);
    }


    @Test
    void initRoverLanding() {

        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        Coordinates target = new Coordinates(2, 3);

        Matcher<RoverLandedEvent> payloadMatcher = predicate(e ->
                    e.getSurfaceId().equals(surfaceId) && e.getDirection() == NORTH
                    && e.getTargetCoordinates().equals(target));

        InitRoverLandingCommand command = new InitRoverLandingCommand(surfaceId, target);

        fixtureConfiguration.given().when(command)
            .expectResultMessageMatching(messageWithPayload(instanceOf(MarsRoverId.class)))
            .expectEventsMatching(payloadsMatching(exactSequenceOf(payloadMatcher, andNoMore())));
    }


    @Test
    void turnRover() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        Coordinates target = new Coordinates(2, 3);

        RoverLandedEvent roverLandedEvent = new RoverLandedEvent(roverId, surfaceId, NORTH, target);

        TurnRoverCommand turnRoverCommand = new TurnRoverCommand(roverId, RIGHT);
        RoverTurnedEvent roverTurnedEvent = new RoverTurnedEvent(roverId, RIGHT);

        fixtureConfiguration
            .given(roverLandedEvent)
            .when(turnRoverCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(roverTurnedEvent);
    }


    @Test
    void collisionOnRover() {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());
        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());
        Coordinates target = new Coordinates(2, 3);

        RoverLandedEvent roverLandedEvent = new RoverLandedEvent(roverId, surfaceId, NORTH, target);

        CollisionOnRoverCommand collisionOnMovingRoverCommand = new CollisionOnRoverCommand(roverId, true);
        RoverDamagedOnCollisionEvent movingRoverDamagedFirstTime = new RoverDamagedOnCollisionEvent(roverId, 10);

        // moving rover collides first time
        fixtureConfiguration
            .given(roverLandedEvent)
            .when(collisionOnMovingRoverCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(movingRoverDamagedFirstTime);

        RoverDamagedOnCollisionEvent movingRoverDamagedSecondTime = new RoverDamagedOnCollisionEvent(roverId, 20);

        // moving rover collides second time
        fixtureConfiguration
            .given(roverLandedEvent, movingRoverDamagedFirstTime)
            .when(collisionOnMovingRoverCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(movingRoverDamagedSecondTime);

        // collision on passive (non-moving) rover
        CollisionOnRoverCommand collisionOnPassiveRoverCommand = new CollisionOnRoverCommand(roverId, false);
        RoverDamagedOnCollisionEvent passiveRoverDamaged = new RoverDamagedOnCollisionEvent(roverId, 25);

        fixtureConfiguration
            .given(roverLandedEvent, movingRoverDamagedFirstTime, movingRoverDamagedSecondTime)
            .when(collisionOnPassiveRoverCommand)
            .expectSuccessfulHandlerExecution()
            .expectEvents(passiveRoverDamaged);
    }
}
