package javachallenge.client.teamcli;

import javachallenge.client.Client;
import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.util.Cell;
import javachallenge.util.Direction;
import javachallenge.util.Edge;
import javachallenge.util.EdgeType;

public class TeamClient extends Client {

    private boolean isDFSArrive = false;
    boolean[][] flags;

    private Direction findPath(Cell source, Cell destination) {
        Direction[] dir = Direction.values();
        flags[source.getX()][source.getY()] = true;

        for (int i = 0; i < 6; i++) {
            try {
                Cell neighborCell = map.getNeighborCell(source, dir[i]);
                Edge neighborEdge = source.getEdge(dir[i]);
                if (neighborCell != null && !flags[neighborCell.getX()][neighborCell.getY()] &&
                        neighborEdge.getType() == EdgeType.OPEN && neighborCell.isGround()) {
                    if (neighborCell.equals(destination)) {
                        isDFSArrive = true;
                        return dir[i];
                    } else
                        findPath(neighborCell, destination);
                    if (isDFSArrive)
                        return dir[i];
                }
            } catch (CellIsNullException e) { }
        }
        System.out.println("Returning null");
        return null;
    }

    @Override
    public void step() {
//        for (int i = 0; i < Math.min(7, getMyUnits().size()); i++) {
//            flags = new boolean[map.getSizeX()][map.getSizeY()];
//            move(getMyUnits().get(i), findPath(getMyUnits().get(i).getCell(), getMap().getMines().get(0)));
//        }
//        if (getTurn() > 1 && getTurn() < 17) {
//            if (getTurn() % 2 == 0) {
//                move(getMyUnits().get(0), Direction.SOUTHWEST);
//            } else {
//                move(getMyUnits().get(0), Direction.SOUTHEAST);
//            }
//            attack(getMyUnits().get(0), Direction.SOUTHEAST);
//        } else if (getTurn() > 16) {
//            move(getMyUnits().get(0), Direction.EAST);
//        }
//        if (getTurn() > 20 && getMyUnits().get(0).getTeamId() == 1) {
//            move(getMyUnits().get(2), Direction.EAST);
//        } else if (getTurn() > 20 && getMyUnits().get(0).getTeamId() == 0)
//            move(getMyUnits().get(1), Direction.EAST);
//        if (getTurn() > 6) {
//            if (getMyUnits().get(0).getTeamId() == 0)
//                attack(getMyUnits().get(0), Direction.SOUTHEAST);
//            else
//                attack(getMyUnits().get(1), Direction.NORTHWEST);
//        }
//        if (getTurn() > 1)
//            if (getTurn() < 5)
//                move(getMyUnits().get(0), Direction.SOUTHEAST);
//            else if (getTurn() < 20)
//                move(getMyUnits().get(0), Direction.EAST);
//            else if (getTurn() < 25)
//                move(getMyUnits().get(0), Direction.SOUTHWEST);
//            else if (getTurn() == 25)
//                move(getMyUnits().get(0), Direction.WEST);
//        if (getTurn() == 20 && getMyUnits().get(0).getTeamId() == 1)
//            move(getMyUnits().get(0), Direction.NORTHEAST);
        try {
            if (getTurn() % 6 == 0 && getTurn() > 1 && getMyUnits().get(0).getTeamId() == 0) {
                makeWall(map.getCellAt(3, 5), Direction.EAST);
                makeWall(map.getCellAt(3, 5), Direction.SOUTHEAST);
                makeWall(map.getCellAt(3, 5), Direction.SOUTHWEST);
                makeWall(map.getCellAt(3, 5), Direction.WEST);
                makeWall(map.getCellAt(3, 5), Direction.NORTHWEST);
                makeWall(map.getCellAt(3, 5), Direction.NORTHEAST);
            } else if ((getTurn() % 6 == 3 && getTurn() > 1 && getMyUnits().get(0).getTeamId() == 0)){
                destroyWall(map.getCellAt(3, 5), Direction.EAST);
                destroyWall(map.getCellAt(3, 5), Direction.SOUTHEAST);
                destroyWall(map.getCellAt(3, 5), Direction.SOUTHWEST);
                destroyWall(map.getCellAt(3, 5), Direction.WEST);
                destroyWall(map.getCellAt(3, 5), Direction.NORTHWEST);
                destroyWall(map.getCellAt(3, 5), Direction.NORTHEAST);
            }
            if (getTurn() > 1)
                move(getMyUnits().get(0), Direction.SOUTHEAST);
        } catch (CellIsNullException e) {
            e.printStackTrace();
        }
    }
}
