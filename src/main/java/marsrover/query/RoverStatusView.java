package marsrover.query;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class RoverStatusView {

    @Id
    private String uuid;
    private String direction;
    private int damage;
    private int x;
    private int y;

    public String getUuid() {

        return uuid;
    }


    public void setUuid(String uuid) {

        this.uuid = uuid;
    }


    public String getDirection() {

        return direction;
    }


    public void setDirection(String direction) {

        this.direction = direction;
    }


    public int getDamage() {

        return damage;
    }


    public void setDamage(int damage) {

        this.damage = damage;
    }


    public int getX() {

        return x;
    }


    public void setX(int x) {

        this.x = x;
    }


    public int getY() {

        return y;
    }


    public void setY(int y) {

        this.y = y;
    }


    @Override
    public String toString() {

        return String.format("Rover %s at coordinates %d,%d facing %s (damage: %2d)", uuid, x, y, direction, damage);
    }
}
