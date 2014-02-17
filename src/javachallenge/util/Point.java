package javachallenge.util;

import java.io.Serializable;

/**
 * Created by mohammad on 2/5/14.
 */
public class Point
        implements Serializable {

    private static final long serialVersionUID = -1410766155888091821L;
    public int x;
    public int y;

    public Point() {
        this(0, 0);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return String.format("Point(%d, %d)", this.x, this.y);
    }

    public int hashCode() {
        int prime = 5003;
        int result = 1;
        result = prime * result + this.x;
        result = prime * result + this.y;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        return this.x == other.x && this.y == other.y;
    }
}