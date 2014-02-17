package javachallenge.message;

import javachallenge.util.Direction;
import javachallenge.util.Point;

import java.io.Serializable;

public class Action implements Serializable {

    private ActionType type;
    private Direction direction = null;
    private Point position;
    private int teamId = 0;

    public Action(ActionType type, Point position, Direction direction, int teamId) {
        this.type = type;
        this.direction = direction;
        this.position = position;
        this.teamId = teamId;
    }

    public boolean isValid() {
        return (direction != null && position != null && type != null);
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public String toString() {
        return "ACTION: " + type.toString() + " " + direction.toString() + " " + position;
    }
}
