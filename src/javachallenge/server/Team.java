package javachallenge.server;

import javachallenge.units.Unit;
import javachallenge.util.Cell;

import java.util.ArrayList;

/**
 * Created by peyman on 2/11/14.
 */
public class Team {
    private int teamId;
    private int resources;
//    private Cell spawn;
//    private Cell destination;
    private ArrayList<Unit> units;
    private int arrivedUnitsNum = 0;
    private int unitID = 0;

    public Team(int teamId, int resources) {
        this.teamId = teamId;
        this.resources = resources;
        units = new ArrayList<Unit>();
    }

    public int getTeamId() {
        return teamId;
    }

    public Unit addUnit(){
        Unit newUnit = new Unit();
        newUnit.setTeamId(this.teamId);
        newUnit.setId(unitID++);
        this.units.add(newUnit);
        return newUnit;
    }

    public int getResources() {
        return resources;
    }

//    public Cell getSpawn() {
//        return spawn;
//    }
//
//    public void setSpawn(Cell spawn) {
//        this.spawn = spawn;
//    }
//
//    public Cell getDestination() {
//        return destination;
//    }
//
//    public void setDestination(Cell destination) {
//        this.destination = destination;
//    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public void decreaseResources (int input) {
        resources -= input;
    }

    public void increaseResources (int input) {
        resources += input;
    }

    public int getArrivedUnitsNum() {
        return arrivedUnitsNum;
    }

    public void increaseArrivedNumber() {
        arrivedUnitsNum++;
    }
}
