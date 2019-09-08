package marsrover.coreapi.rover

import marsrover.command.Coordinates
import marsrover.command.Direction
import marsrover.command.Identifier
import marsrover.command.rover.MarsRoverId
import marsrover.command.rover.Turn
import marsrover.command.surface.Dimension
import marsrover.command.surface.SurfaceId


data class RoverLandedEvent(
        val roverId: MarsRoverId,
        val surfaceId: SurfaceId,
        val direction: Direction,
        val targetCoordinates: Coordinates
)

data class RoverTurnedEvent(
        val roverId: MarsRoverId,
        val turn: Turn
)

data class RoverMovementStartedEvent(
        val roverId: MarsRoverId,
        val surfaceId: SurfaceId,
        val direction: Direction,
        val movementId: String
)

data class RoverDamagedOnCollisionEvent(
        val roverId: MarsRoverId,
        val damage: Int
)


data class SurfaceCreatedEvent(
        val surfaceId: SurfaceId,
        val dimension: Dimension
)

data class ObjectPlacedEvent(
        val surfaceId: SurfaceId,
        val objectId: Identifier,
        val targetCoordinates: Coordinates
)

data class ObjectMovedEvent(
        val surfaceId: SurfaceId,
        val objectId: Identifier,
        val movementId: String,
        val sourceCoordinates: Coordinates,
        val targetCoordinates: Coordinates
)

data class ObjectsCollidedEvent(
        val surfaceId: SurfaceId,
        val objectId: Identifier,
        val movementId: String,
        val movingObjectId: Identifier,
        val passiveObjectId: Identifier
)
