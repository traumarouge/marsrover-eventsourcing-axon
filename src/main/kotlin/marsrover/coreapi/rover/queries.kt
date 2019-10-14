package marsrover.coreapi.rover

import marsrover.command.rover.MarsRoverId


data class GetRoverStatusQuery(
        val roverId: MarsRoverId
)