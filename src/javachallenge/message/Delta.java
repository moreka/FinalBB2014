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
    private Point source;
    private Point destination;
    private Point destinationWallie;
    private int mineChange;
    private int teamID;
    private int unitID;
    private int changeValue;

    public int getChangeValue() {
        return changeValue;
    }

    public Delta(DeltaType type, int teamID, int changeValue) {
        this.type = type;
        this.teamID = teamID;
        this.changeValue = changeValue;
    }

    public  Delta(DeltaType type, Point source, int teamID, int unitID) {
        this.type = type;
        this.source = source;
        this.teamID = teamID;
        this.unitID = unitID;
    }

    public Delta(DeltaType type, Point source) {
        this(type, source, null, null, 0);
    }

    public Delta(DeltaType type, Point mineCell, int mineChange) {
        this(type, mineCell, null, null, mineChange);
    }

    public Delta(DeltaType type, Point source, Point destination) {
        this(type, source, destination, null, 0);
    }

    public Delta(DeltaType type, Point source, Point destination, Point destinationWallie) {
        this(type, source, destination, destinationWallie, 0);
    }

    public Delta(DeltaType type, Point source, Point destination, Point destinationWallie, int mineChange) {
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.destinationWallie = destinationWallie;
        this.mineChange = mineChange;
    }

    public Point getDestination() {
        return this.destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public DeltaType getType() {
        return type;
    }

    public void setType(DeltaType type) {
        this.type = type;
    }

    public Point getSource() {
        return source;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public int getMineChange() {
        return mineChange;
    }

    public void setMineChange(int mineChange) {
        this.mineChange = mineChange;
    }

    public Point getDestinationWallie() {
        return destinationWallie;
    }

    public void setDestinationWallie(Point destinationWallie) {
        this.destinationWallie = destinationWallie;
    }

    public int getTeamID() {
        return teamID;
    }

    public int getUnitID() {
        return unitID;
    }
}
