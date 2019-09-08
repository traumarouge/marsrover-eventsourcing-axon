package marsrover.command;

import java.util.Objects;
import java.util.UUID;


public class Identifier {

    private final String uuid;

    public Identifier(String uuid) {

        // noinspection ResultOfMethodCallIgnored
        UUID.fromString(uuid);

        this.uuid = uuid;
    }

    @Override
    public final boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Identifier)) {
            return false;
        }

        Identifier other = (Identifier) o;

        return uuid.equals(other.uuid);
    }


    @Override
    public final int hashCode() {

        return Objects.hash(uuid);
    }


    @Override
    public final String toString() {

        return uuid;
    }
}
