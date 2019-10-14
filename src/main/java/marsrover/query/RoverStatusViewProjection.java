package marsrover.query;

import marsrover.coreapi.rover.GetRoverStatusQuery;
import marsrover.coreapi.rover.ObjectMovedEvent;
import marsrover.coreapi.rover.RoverDamagedOnCollisionEvent;
import marsrover.coreapi.rover.RoverLandedEvent;

import org.axonframework.eventhandling.EventHandler;

import org.axonframework.queryhandling.QueryHandler;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class RoverStatusViewProjection {

    private final RoverStatusViewRepository repository;

    @Autowired
    public RoverStatusViewProjection(RoverStatusViewRepository repository) {

        this.repository = repository;
    }

    @EventHandler
    public void on(RoverLandedEvent event) {

        RoverStatusView statusView = new RoverStatusView();

        statusView.setUuid(event.getRoverId().toString());
        statusView.setDirection(event.getDirection().toString());
        statusView.setX(event.getTargetCoordinates().x);
        statusView.setY(event.getTargetCoordinates().y);

        repository.save(statusView);
    }


    @EventHandler
    public void on(ObjectMovedEvent event) {

        Optional<RoverStatusView> byId = repository.findById(event.getObjectId().toString());

        byId.ifPresent(roverStatusView -> {
            roverStatusView.setX(event.getTargetCoordinates().x);
            roverStatusView.setY(event.getTargetCoordinates().y);
        });
    }


    @EventHandler
    public void on(RoverDamagedOnCollisionEvent event) {

        Optional<RoverStatusView> byId = repository.findById(event.getRoverId().toString());

        byId.ifPresent(roverStatusView -> { roverStatusView.setDamage(event.getDamage()); });
    }


    @QueryHandler
    public RoverStatusView on(GetRoverStatusQuery query) {

        return repository.findById(query.getRoverId().toString()).orElse(null);
    }
}
