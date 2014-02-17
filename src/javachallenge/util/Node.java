package javachallenge.util;

import java.io.Serializable;

public class Node implements Serializable {

    private Point point;
    private Edge[] edges = new Edge[6];

    //private UnitWallie unitWallie;

    public Node(int x, int y) {
        this.point = new Point(x, y);
        for(int i = 0; i < 6; i++)
            edges[i] = new Edge();
    }

    public Edge getEdge(NodeDirection dir) {
        return edges[dir.ordinal()];
    }

    public void setEdge(Edge edge, NodeDirection dir) {
        this.edges[dir.ordinal()] = edge;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        return this.point == other.point;
    }
}
