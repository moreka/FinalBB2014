package javachallenge.util;

import javachallenge.exceptions.UnitIsNullException;
import javachallenge.units.Unit;

import java.io.Serializable;

/**
 * Created by peyman on 2/6/14.
 */
public class Cell implements Serializable {

    private Point point;
    private Unit unit;
    private CellType type;
    private Edge[] edges = new Edge[6];


    public Cell(int x, int y, CellType type) {
        this.point = new Point(x, y);
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


    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public int getX() {
        return point.getX();
    }

    public void setX(int x) {
        point.setX(x);
    }

    public int getY() {
        return point.getY();
    }

    public void setY(int y) {
        point.setY(y);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cell other = (Cell) obj;
        return this.point == other.point;
    }
}
