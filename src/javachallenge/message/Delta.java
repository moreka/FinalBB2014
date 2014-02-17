package javachallenge.message;

import javachallenge.util.*;

import java.io.Serializable;

/**
 * Created by mohammad on 2/6/14.
 */
public class Delta
        implements Serializable {

    private static final long serialVersionUID = -4314305772186897307L;
    private DeltaType type;
    private Point point;
    private Direction direction;
    private int changeValue;
    private int teamID;
    private int unitID;

    public Delta(DeltaType type, Point point, Direction direction, int changeValue, int teamID, int unitID) {
        this.type = type;
        this.point = point;
        this.direction = direction;
        this.changeValue = changeValue;
        this.teamID = teamID;
        this.unitID = unitID;
    }

    /**
     * Constructor used in making walls
     * @param type
     * @param position
     * @param direction
     */
    public Delta(DeltaType type, Point position, Direction direction) {
        this(type, position, direction, 0, 0, 0);
    }

    public DeltaType getType() {
        return type;
    }

    public void setType(DeltaType type) {
        this.type = type;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(int changeValue) {
        this.changeValue = changeValue;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public int getUnitID() {
        return unitID;
    }

    public void setUnitID(int unitID) {
        this.unitID = unitID;
    }
}
