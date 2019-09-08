package marsrover;

import marsrover.command.Coordinates;

import marsrover.command.rover.MarsRoverId;

import marsrover.command.surface.Dimension;
import marsrover.command.surface.SurfaceId;

import marsrover.coreapi.rover.CreateSurfaceCommand;
import marsrover.coreapi.rover.InitRoverLandingCommand;
import marsrover.coreapi.rover.MoveRoverCommand;

import org.axonframework.commandhandling.gateway.CommandGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


@SpringBootApplication
public class MarsRoverApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarsRoverApplication.class);

    @Autowired
    private CommandGateway commandGateway;

    public static void main(String[] args) {

        SpringApplication.run(MarsRoverApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        SurfaceId surfaceId = createSurface(new Dimension(10, 10));

        MarsRoverId roverOneId = landRover(surfaceId, new Coordinates(2, 0));
        MarsRoverId roverTwoId = landRover(surfaceId, new Coordinates(2, 2));

        moveRover(roverOneId);
        moveRover(roverOneId);
        moveRover(roverOneId);
        moveRover(roverOneId);
        moveRover(roverOneId);
    }


    private SurfaceId createSurface(Dimension dimension) throws ExecutionException, InterruptedException {

        CreateSurfaceCommand command = new CreateSurfaceCommand(dimension);
        CompletableFuture<SurfaceId> future = commandGateway.send(command);

        return future.get();
    }


    private MarsRoverId landRover(SurfaceId surfaceId, Coordinates coordinates) throws ExecutionException,
        InterruptedException {

        CompletableFuture<MarsRoverId> future = commandGateway.send(new InitRoverLandingCommand(surfaceId,
                    coordinates));

        return future.get();
    }


    private void moveRover(MarsRoverId roverId) throws InterruptedException {

        commandGateway.send(new MoveRoverCommand(roverId));
        Thread.sleep(100);
    }
}
