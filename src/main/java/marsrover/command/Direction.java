package marsrover.command;

public enum Direction {

    NORTH("N"),
    EAST("E"),
    SOUTH("S"),
    WEST("W");

    public final String symbol;

    Direction(String symbol) {

        this.symbol = symbol;
    }

    public Direction leftOf() {

        switch (this) {
            case NORTH:
                return WEST;

            case EAST:
                return NORTH;

            case SOUTH:
                return EAST;

            case WEST:
                return SOUTH;
        }

        throw new IllegalStateException();
    }


    public Direction rightOf() {

        switch (this) {
            case NORTH:
                return EAST;

            case EAST:
                return SOUTH;

            case SOUTH:
                return WEST;

            case WEST:
                return NORTH;
        }

        throw new IllegalStateException();
    }
}
