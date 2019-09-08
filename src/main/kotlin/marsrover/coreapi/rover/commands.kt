package marsrover.coreapi.rover

import marsrover.command.Coordinates
import marsrover.command.Direction
import marsrover.command.Identifier
import marsrover.command.rover.MarsRoverId
import marsrover.command.rover.Turn
import marsrover.command.surface.Dimension
import marsrover.command.surface.SurfaceId
import org.axonframework.modelling.command.TargetAggregateIdentifier


data class InitRoverLandingCommand(
        val surfaceId: SurfaceId,
        val targetCoordinates: Coordinates
)

data class TurnRoverCommand(
        @TargetAggregateIdentifier
        val roverId: MarsRoverId,
        val turn: Turn
)

data class MoveRoverCommand(
        @TargetAggregateIdentifier
        val roverId: MarsRoverId
)

data class CollisionOnRoverCommand(
        @TargetAggregateIdentifier
        val roverId: MarsRoverId,
        val isRoverInMotion: Boolean
)


data class CreateSurfaceCommand(
        val dimension: Dimension
)

data class PlaceObjectCommand(
        @TargetAggregateIdentifier
        val surfaceId: SurfaceId,
        val objectId: Identifier,
        val targetCoordinates: Coordinates
)

data class MoveObjectCommand(
        @TargetAggregateIdentifier
        val surfaceId: SurfaceId,
        val objectId: Identifier,
        val direction: Direction,
        val movementId: String
)
