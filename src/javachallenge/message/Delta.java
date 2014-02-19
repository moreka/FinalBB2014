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

    /**
     * Constructor used for changing resources of a team
     * @param type
     * @param teamID
     * @param value
     */
    public Delta(DeltaType type, int teamID, int value) {
        this(type, null, null, value, teamID, 0);
    }

    /**
     * Constructor used for arriving or killing units
     * @param type
     * @param point
     */
    public Delta(DeltaType type, Point point) {
        this(type, point, null);
    }

    /**
     * Constructor used for changing mine amount
     * @param type
     * @param point
     * @param mineRate
     */
    public Delta(DeltaType type, Point point, int mineRate) {
        this(type, point, null, mineRate, 0, 0);
    }

    /**
     * Constructor used for spawning new units
     * @param type
     * @param point
     * @param teamId
     * @param unitId
     */
    public Delta(DeltaType type, Point point, int teamId, int unitId) {
        this(type, point, null, 0, teamId, unitId);
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

    @Override
    public String toString() {
        String string = null;
        switch (this.getType()) {
            case SPAWN:
                string = "Spawning a new unit with ID " + unitID + " for team " + teamID;
            break;
            case RESOURCE_CHANGE:
                string = "Team " + teamID + " resource has change by " + changeValue;
            break;
            case MINE_CHANGE:
                string = "Mine " + point + " resource has change by " + changeValue;
            break;
            case MINE_DISAPPEAR:
                string = "Mine " + point + " disappeared";
            break;
            case AGENT_ARRIVE:
                string = "Unit " + unitID + " from team " + teamID + " has arrived to destination";
            break;
            case AGENT_ATTACK:
                string = "Unit " + unitID + " from team " + teamID + " attacked to " + direction + " Direction";
            break;
            case AGENT_KILL:
                string = "Unit " + unitID + " from team " + teamID + " has killed";
            break;
            case CELL_MOVE:
                string = "Unit " + unitID + " from team " + teamID + " moved to " + direction + " Direction";
            break;
            case WALL_DESTROY:
                string = "WallDestroy";
            break;
            case WALL_MAKE:
                string = "WallMake";
            break;
        }
        return string;
        /*return "Delta{" +
                "type=" + type +
                ", point=" + point +
                ", direction=" + direction +
                ", changeValue=" + changeValue +
                ", teamID=" + teamID +
                ", unitID=" + unitID +
                '}';*/
    }
}
