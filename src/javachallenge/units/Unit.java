package javachallenge.units;

import javachallenge.util.Cell;
import javachallenge.util.Direction;

public class Unit {
    private int id;
    private int teamId;
    private Direction move;
    private boolean arrived = false;
    private Cell cell;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Direction getMove() {
        return move;
    }

    public void setMove(Direction move) {
        this.move = move;
    }

    public boolean isArrived() {
        return arrived;
    }

    public void setArrived(boolean arrived) {
        this.arrived = arrived;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
