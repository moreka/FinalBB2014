package javachallenge.server;

import javachallenge.units.Unit;
import javachallenge.util.Cell;

import java.util.ArrayList;

/**
 * Created by peyman on 2/11/14.
 */
public class Team {
    private int teamId;
    private int resource;
    private int score;
    private Cell spawn;
    private Cell destination;
    private ArrayList<Unit> units;
    private int arrivedUnitsNum = 0;
    private int unitID = 0;
    private String name;

    public Team(int teamId, int resource) {
        this.teamId = teamId;
        this.resource = resource;
        units = new ArrayList<Unit>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
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

    public int getResource() {
        return resource;
    }

    public Cell getSpawn() {
        return spawn;
    }

    public void setSpawn(Cell spawn) {
        this.spawn = spawn;
    }

    public Cell getDestination() {
        return destination;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

    public void decreaseResources (int input) {
        resource -= input;
    }

    public void increaseResources (int input) {
        resource += input;
    }

    public int getArrivedUnitsNum() {
        return arrivedUnitsNum;
    }

    public void increaseArrivedNumber() {
        arrivedUnitsNum++;
    }

    public void updateScore(int delta) {
        this.score += delta;
    }

    public int getScore() {
        return score;
    }
}
