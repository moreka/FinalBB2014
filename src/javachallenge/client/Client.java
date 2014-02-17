package javachallenge.client;

import javachallenge.message.*;
import javachallenge.units.Unit;
import javachallenge.util.*;

import java.util.ArrayList;

/**
 * Created by mohammad on 2/5/14.
 */
public abstract class Client {
    protected ArrayList<Action> actionList;
    protected Map map;
    protected ArrayList<Unit> myUnits = new ArrayList<Unit>();
    protected int teamID;
    protected int resources;

    public Client() {
    }

    public void init() {
        actionList = new ArrayList<Action>();
    }

    public abstract void step();

    public void update(ServerMessage message) {
        this.map.updateMap(message.getWallDeltaList());
        this.map.updateMap(message.getMoveDeltaList());
        this.map.updateMap(message.getOtherDeltaList());

        for (Delta d : message.getOtherDeltaList()) {
            if (d.getType() == DeltaType.SPAWN && d.getTeamID() == this.getTeamID()) {
                myUnits.add(map.getCellAt(d.getSource().getX(), d.getSource().getY()).getUnit());
            }
            else if(d.getType() == DeltaType.RESOURCE_CHANGE && d.getTeamID() == this.getTeamID()){
                this.resources = this.resources + d.getChangeValue();
            }
            for (int i = myUnits.size() - 1; i >= 0; i--) {
                if (myUnits.get(i).getCell() == null)
                    myUnits.remove(i);
            }
        }
    }

    public ClientMessage end() {
        return new ClientMessage(actionList);
    }
    public void move(Unit unit, Direction direction) {
        if (!unit.isArrived())
            actionList.add(new Action(ActionType.MOVE, new Point(unit.getCell().getX(), unit.getCell().getY()), direction));
    }

    public void makeWall(Cell cell, Direction direction) {
        Node[] nodes = map.getNodesFromCellAt(cell, direction);
        actionList.add(new Action(ActionType.MAKE_WALL, new Point(nodes[0].getX(), nodes[0].getY()), map.getDirectionFromTwoNodes(nodes[0], nodes[1])));
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public int getResources() {
        return resources;
    }

    public void setResources(int resources) {
        this.resources = resources;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }
}
