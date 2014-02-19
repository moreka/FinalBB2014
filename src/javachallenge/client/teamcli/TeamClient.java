package javachallenge.client.teamcli;

import javachallenge.client.Client;
import javachallenge.exceptions.CellIsNullException;
import javachallenge.util.Cell;
import javachallenge.util.Direction;
import javachallenge.util.Edge;
import javachallenge.util.EdgeType;

public class TeamClient extends Client {

    private boolean isDFSArrive = false;
    boolean[][] flags = new boolean[map.getSizeX()][map.getSizeY()];

    private Direction findPath(Cell source, Cell destination) {
        Direction[] dir = Direction.values();
        flags[source.getX()][source.getY()] = true;
        outer: for (int i = 0; i < 6; i++) {
            try {
                Cell neighborCell = map.getNeighborCell(source, dir[i]);
                Edge neighborEdge = source.getEdge(dir[i]);
                if (neighborCell != null && flags[neighborCell.getX()][neighborCell.getY()] == false &&
                        neighborEdge.getType() == EdgeType.OPEN && neighborCell.isGround()) {
                    if (neighborCell.equals(destination)) {
                        isDFSArrive = true;
                        return dir[i];
                    }
                    else
                        findPath(neighborCell, destination);
                    if (isDFSArrive)
                        return dir[i];
                }
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
        return null;
    }

    @Override
    public void step() {
        
    }
}
