package javachallenge.client;

import javachallenge.exceptions.CellIsNullException;
import javachallenge.exceptions.UnitIsNullException;
import javachallenge.message.*;
import javachallenge.units.Unit;
import javachallenge.util.*;

import java.util.ArrayList;

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

        for (Delta delta : message.getOtherDeltaList()) {
            if (delta.getType() == DeltaType.SPAWN && delta.getTeamID() == this.getTeamID()) {
                try {
                    myUnits.add(map.getCellAtPoint(delta.getPoint()).getUnit());
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
            else if(delta.getType() == DeltaType.RESOURCE_CHANGE && delta.getTeamID() == this.getTeamID()){
                this.resources = this.resources + delta.getChangeValue();
            }
        }
    }

    public ClientMessage end() {
        return new ClientMessage(actionList);
    }
    public void move(Unit unit, Direction direction) {
        if (!unit.isArrived())
            actionList.add(new Action(
                    ActionType.MOVE,
                    unit.getCell().getPoint(),
                    direction,
                    this.getTeamID())
            );
    }

    public void makeWall(Cell cell, Direction direction) {
        actionList.add(new Action(
                ActionType.MAKE_WALL,
                cell.getPoint(),
                direction,
                getTeamID())
        );
    }

    public void attack(Cell cell, Direction direction){
        actionList.add(new Action(
                ActionType.ATTACK,
                cell.getPoint(),
                direction,
                getTeamID()));
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
