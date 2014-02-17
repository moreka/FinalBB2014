package javachallenge.util;

import java.io.Serializable;

/**
 * Created by peyman on 2/6/14.
 */
public class Node implements Serializable {
    private int x;
    private int y;
    //private UnitWallie unitWallie;
    private Edge[] edges = new Edge[6];

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        for(int i = 0; i < 6; i++)
            edges[i] = new Edge();
    }

    public Edge getEdge(NodeDirection dir) {
        return edges[dir.ordinal()];
    }

    public void setEdge(Edge edge, NodeDirection dir) {
        this.edges[dir.ordinal()] = edge;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        return this.x == other.x && this.y == other.y;
    }
}
