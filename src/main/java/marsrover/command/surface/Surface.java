package marsrover.command.surface;

import marsrover.command.Coordinates;
import marsrover.command.Direction;
import marsrover.command.Identifier;

import marsrover.coreapi.rover.CreateSurfaceCommand;
import marsrover.coreapi.rover.MoveObjectCommand;
import marsrover.coreapi.rover.ObjectMovedEvent;
import marsrover.coreapi.rover.ObjectPlacedEvent;
import marsrover.coreapi.rover.ObjectsCollidedEvent;
import marsrover.coreapi.rover.PlaceObjectCommand;
import marsrover.coreapi.rover.SurfaceCreatedEvent;

import org.axonframework.commandhandling.CommandHandler;

import org.axonframework.eventsourcing.EventSourcingHandler;

import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;

import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Aggregate
public class Surface {

    @AggregateIdentifier
    private SurfaceId id;
    private Dimension dimension;
    private Map<Coordinates, Identifier> objectsByCoordinates;

    protected Surface() {

        // required by Axon
    }


    @CommandHandler
    public Surface(CreateSurfaceCommand command) {

        SurfaceId surfaceId = new SurfaceId(UUID.randomUUID().toString());

        AggregateLifecycle.apply(new SurfaceCreatedEvent(surfaceId, command.getDimension()));
    }

    @CommandHandler
    public void handle(PlaceObjectCommand command) {

        AggregateLifecycle.apply(new ObjectPlacedEvent(id, command.getObjectId(), command.getTargetCoordinates()));
    }


    @CommandHandler
    public void handle(MoveObjectCommand command) {

        Identifier objectToMove = command.getObjectId();
        Coordinates sourceCoordinates = coordinates(objectToMove);

        Coordinates targetCoordinates = adjacentCoordinates(sourceCoordinates, command.getDirection());
        Identifier objectAtTargetCoordinates = objectsByCoordinates.get(targetCoordinates);

        if (objectAtTargetCoordinates != null) {
            AggregateLifecycle.apply(new ObjectsCollidedEvent(id, objectToMove, command.getMovementId(), objectToMove,
                    objectAtTargetCoordinates));
        } else {
            AggregateLifecycle.apply(new ObjectMovedEvent(id, objectToMove, command.getMovementId(), sourceCoordinates,
                    targetCoordinates));
        }
    }


    @EventSourcingHandler
    private void on(SurfaceCreatedEvent event) {

        id = event.getSurfaceId();
        dimension = event.getDimension();
        objectsByCoordinates = new HashMap<>();
    }


    @EventSourcingHandler
    private void on(ObjectPlacedEvent event) {

        objectsByCoordinates.put(event.getTargetCoordinates(), event.getObjectId());
    }


    @EventSourcingHandler
    private void on(ObjectMovedEvent event) {

        objectsByCoordinates.remove(coordinates(event.getObjectId()));
        objectsByCoordinates.put(event.getTargetCoordinates(), event.getObjectId());
    }


    private Coordinates coordinates(Identifier identifier) {

        for (Map.Entry<Coordinates, Identifier> e : objectsByCoordinates.entrySet()) {
            if (e.getValue().equals(identifier)) {
                return e.getKey();
            }
        }

        throw new IllegalArgumentException("Cannot move what is not on surface");
    }


    private Coordinates adjacentCoordinates(Coordinates c, Direction direction) {

        switch (direction) {
            case NORTH:
                return c.y == dimension.height - 1 ? new Coordinates(c.x, 0) : new Coordinates(c.x, c.y + 1);

            case EAST:
                return c.x == dimension.width - 1 ? new Coordinates(0, c.y) : new Coordinates(c.x + 1, c.y);

            case SOUTH:
                return c.y == 0 ? new Coordinates(c.x, dimension.height - 1) : new Coordinates(c.x, c.y - 1);

            case WEST:
                return c.x == 0 ? new Coordinates(dimension.width - 1, c.y) : new Coordinates(c.x - 1, c.y);
        }

        throw new IllegalStateException();
    }
}
