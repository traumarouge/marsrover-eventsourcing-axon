package marsrover.command.rover;

import marsrover.command.Direction;

import marsrover.command.surface.SurfaceId;

import marsrover.coreapi.rover.CollisionOnRoverCommand;
import marsrover.coreapi.rover.InitRoverLandingCommand;
import marsrover.coreapi.rover.MoveRoverCommand;
import marsrover.coreapi.rover.RoverDamagedOnCollisionEvent;
import marsrover.coreapi.rover.RoverLandedEvent;
import marsrover.coreapi.rover.RoverMovementStartedEvent;
import marsrover.coreapi.rover.RoverTurnedEvent;
import marsrover.coreapi.rover.TurnRoverCommand;

import org.axonframework.commandhandling.CommandHandler;

import org.axonframework.eventsourcing.EventSourcingHandler;

import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;

import org.axonframework.spring.stereotype.Aggregate;

import java.util.UUID;


@Aggregate
public class MarsRover {

    @AggregateIdentifier
    private MarsRoverId id;
    private SurfaceId surfaceId;
    private Direction direction;
    private int damage;

    protected MarsRover() {

        // required by Axon
    }


    @CommandHandler
    public MarsRover(InitRoverLandingCommand command) {

        MarsRoverId roverId = new MarsRoverId(UUID.randomUUID().toString());

        AggregateLifecycle.apply(new RoverLandedEvent(roverId, command.getSurfaceId(), Direction.NORTH,
                command.getTargetCoordinates()));
    }

    @CommandHandler
    public void handle(TurnRoverCommand command) {

        if (damage >= 25) {
            throw new IllegalStateException("Rover cannot turn. Current damage is too high: " + damage);
        }

        AggregateLifecycle.apply(new RoverTurnedEvent(id, command.getTurn()));
    }


    @CommandHandler
    public void handle(MoveRoverCommand command) {

        if (surfaceId == null) {
            throw new IllegalStateException("Rover cannot move - not yet landed on surface");
        }

        if (damage >= 25) {
            throw new IllegalStateException("Rover cannot move. Current damage is too high: " + damage);
        }

        String movementId = UUID.randomUUID().toString();

        AggregateLifecycle.apply(new RoverMovementStartedEvent(id, surfaceId, direction, movementId));
    }


    @CommandHandler
    void handle(CollisionOnRoverCommand command) {

        int dmg = damage + (command.isRoverInMotion() ? 10 : 5);

        AggregateLifecycle.apply(new RoverDamagedOnCollisionEvent(id, dmg));
    }


    @EventSourcingHandler
    public void on(RoverLandedEvent event) {

        id = event.getRoverId();
        surfaceId = event.getSurfaceId();
        direction = event.getDirection();
        damage = 0;
    }


    @EventSourcingHandler
    public void on(RoverTurnedEvent event) {

        switch (event.getTurn()) {
            case LEFT:
                direction = direction.leftOf();
                break;

            case RIGHT:
                direction = direction.rightOf();
                break;
        }
    }


    @EventSourcingHandler
    public void on(RoverDamagedOnCollisionEvent event) {

        damage = event.getDamage();
    }
}
