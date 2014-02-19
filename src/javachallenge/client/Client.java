package javachallenge.client;

import javachallenge.exceptions.CellIsNullException;
import javachallenge.message.*;
import javachallenge.units.Unit;
import javachallenge.util.*;

import java.util.ArrayList;

public abstract class Client {
    private ArrayList<Action> actionList;
    protected Map map;
    private ArrayList<Unit> myUnits = new ArrayList<Unit>();
    private int resources;
    private int teamID;
    private int turn = 0;

    public Client() {
    }

    public void init() {
        actionList = new ArrayList<Action>();
    }

    public abstract void step();

    public void update(ServerMessage message) {
        this.map.updateMap(message.getAttackDeltaList());
        this.map.updateMap(message.getMoveDeltaList());
        this.map.updateMap(message.getWallDeltaList());
        this.map.updateMap(message.getOtherDeltaList());

        for (Delta delta : message.getOtherDeltaList()) {
            if (delta.getType() == DeltaType.SPAWN && delta.getTeamID() == this.getTeamID()) {
                try {
                    myUnits.add(map.getCellAtPoint(delta.getPoint()).getUnit());
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
            else if (delta.getType() == DeltaType.RESOURCE_CHANGE && delta.getTeamID() == this.getTeamID()){
                this.resources = this.resources + delta.getChangeValue();
            }
            else if ((delta.getType() == DeltaType.AGENT_KILL || delta.getType() == DeltaType.AGENT_ARRIVE)
                    && delta.getTeamID() == this.getTeamID()) {

                for (int i = 0; i < myUnits.size(); i++)
                    if (myUnits.get(i).getId() == delta.getUnitID()) {
                        myUnits.remove(i);
                        break;
                    }
            }
        }

        turn++;
    }

    public ArrayList<Unit> getMyUnits() {
        return myUnits;
    }

    public int getTurn() {
        return turn;
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

    public void destroyWall(Cell cell, Direction direction) {
        actionList.add(new Action(
                ActionType.DESTROY_WALL,
                cell.getPoint(),
                direction,
                getTeamID()));
    }

    public void attack(Unit unit, Direction direction) {
        if (unit == null)
            return;
        actionList.add(new Action(
                ActionType.ATTACK,
                unit.getCell().getPoint(),
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

    public Map getMap() {
        return map;
    }
}
