package javachallenge.util;

import javachallenge.units.Unit;

import java.io.Serializable;

/**
 * Created by peyman on 2/6/14.
 */
public class Cell implements Serializable {

    private int x;
    private int y;
    private Unit unit;
    private CellType type;
    private Edge[] edges = new Edge[6];


    public Cell(int x, int y, CellType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        for(int i = 0; i < 6; i++)
            edges[i] = new Edge();
    }

    public Edge getEdge(Direction dir){
        return this.edges[dir.ordinal()];
    }

    public void setEdge(Edge edge,Direction dir){
        this.edges[dir.ordinal()] = edge;
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public boolean isGround() {
        return this.getType() == CellType.TERRAIN || this.getType() == CellType.SPAWN ||
                this.getType() == CellType.MINE || this.getType() == CellType.DESTINATION;
    }
}
