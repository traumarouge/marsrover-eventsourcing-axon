package marsrover.command;

import java.util.Objects;


public final class Coordinates {

    public final int x;
    public final int y;

    public Coordinates(int x, int y) {

        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Coordinates)) {
            return false;
        }

        Coordinates other = (Coordinates) o;

        return x == other.x && y == other.y;
    }


    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }
}
