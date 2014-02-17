package javachallenge.message;

import javachallenge.units.Unit;
import javachallenge.util.Direction;
import javachallenge.util.NodeDirection;
import javachallenge.util.Point;

import java.io.Serializable;

/**
 * Created by mohammad on 2/6/14.
 */
public class Action implements Serializable {
    private ActionType type;
    private Direction direction = null;
    private NodeDirection nodeDirection = null;
    private Point position;

    public Action(ActionType type, Point position, Direction direction) {
        this(type, position, direction, null);
    }

    public Action(ActionType type, Point position, NodeDirection nodeDirection) {
        this(type, position, null, nodeDirection);
    }

    public Action(ActionType type, Point position, Direction direction, NodeDirection nodeDirection) {
        this.type = type;
        this.direction = direction;
        this.nodeDirection = nodeDirection;
        this.position = position;
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

    public NodeDirection getNodeDirection() {
        return nodeDirection;
    }

    public void setNodeDirection(NodeDirection nodeDirection) {
        this.nodeDirection = nodeDirection;
    }

    public String toString() {
        return "ACTION: " + (type.toString() + " " + ((direction == null) ? "" : direction.toString()) + " " + position);
    }
}
