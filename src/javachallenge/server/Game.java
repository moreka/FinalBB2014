package javachallenge.server;

import javachallenge.mapParser.Parser;
import javachallenge.units.Unit;
import javachallenge.message.Action;
import javachallenge.message.ActionType;
import javachallenge.message.Delta;
import javachallenge.message.DeltaType;
import javachallenge.util.*;
import javachallenge.util.Map;
import mapmaker.MapMaker;

import java.io.IOException;
import java.util.*;

public class Game {

    private static final int MINE_RATE = 4;                 // resource per turn
    private static final int COST_WALL = 10;
    private static final int GAME_LENGTH = 700;             // turn
    private static final int CE_SPAWN_RATE = 2;             // each 2 turn
    private static final int EE_SPAWN_RATE = 1;
    public static final int INITIAL_RESOURCE = 150;

    private boolean ended;
    private Map map;
    private ArrayList<Unit>[][] tempOtherMoves;
    private ArrayList<Delta> wallDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> moveDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> otherDeltas = new ArrayList<Delta>();
    private int[] resources = new int[2];
    private int turn;
    private Team CETeam;
    private Team EETeam;
    private int CEScore = 0;

    public int getTurn() {
        return turn;
    }

    public Team getCETeam() {
        return CETeam;
    }

    public Team getEETeam() {
        return EETeam;
    }

    public boolean isEnded() {
        return ended;
    }

    public Game (Map map){
        this.map = map;
        tempOtherMoves = new ArrayList[map.getSizeX() + 1][map.getSizeY() + 1];
        CETeam = new Team(0, INITIAL_RESOURCE);
        EETeam = new Team(1, 0);
        for (int i = 0; i < map.getSizeX() + 1; i++)
            for (int j = 0; j < map.getSizeY() + 1; j++)
                tempOtherMoves[i][j] = new ArrayList<Unit>();
        MapHelper mapHelper = new MapHelper(map);
        Parser p = new Parser();
        try {
            p.javaToJson(mapHelper, "net.mapHelper");
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Map getMap() {
        return map;
    }

    public void handleActions(ArrayList<Action> actions) {
        ArrayList<Action> moves = new ArrayList<Action>();
        ArrayList<Action> walls = new ArrayList<Action>();
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).getType() == ActionType.MOVE && actions.get(i).getPosition() != null &&
                    actions.get(i).getDirection() != null)
                moves.add(actions.get(i));
            else if(actions.get(i).getType() == ActionType.MAKE_WALL && actions.get(i).getPosition() != null &&
                    actions.get(i).getNodeDirection() != null)
                walls.add(actions.get(i));
        }
        handleMakeWalls(walls);
        map.updateMap(this.getWallDeltasList());
        handleMoves(moves);
        map.updateMap(this.getMoveDeltasList());
    }

    public void handleMakeWalls(ArrayList<Action> walls){
        Collections.shuffle(walls);
        ArrayList<Edge> wallsWantMake = new ArrayList<Edge>();
        for (Action wall : walls) {
            if (!map.isNodeInMap(wall.getPosition()))
                continue;
            Point point1 = new Point(wall.getPosition().getX(), wall.getPosition().getY());
            Node node1 = map.getNodeAt(point1.getX(), point1.getY());
            Node node2 = map.getNeighborNode(node1, wall.getNodeDirection());
            Point point2 = new Point(node2.getX(), node2.getY());
            Edge edge = node1.getEdge(wall.getNodeDirection());
            if (CETeam.getResources() >= COST_WALL && wall.getType() == ActionType.MAKE_WALL &&
                    edge.getType() == EdgeType.OPEN) {
                wallsWantMake.add(edge);
                if (isTherePathAfterThisEdges(map.getSpawnPoint(0), map.getDestinationPoint(0), wallsWantMake) &&
                        isTherePathAfterThisEdges(map.getSpawnPoint(1), map.getDestinationPoint(1), wallsWantMake)) {
                    CETeam.decreaseResources(COST_WALL);
                    wallDeltas.add(new Delta(DeltaType.WALL_DRAW, point1, point2));
                    otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, 0, -COST_WALL));
                    CEScore -= 3;
                }
                else {
                    wallsWantMake.remove(edge);
                }
            }
        }
    }

    private void handleMoves(ArrayList<Action> moves) {
        ArrayDeque<Integer> xOfOverloadedCells = new ArrayDeque<Integer>();
        ArrayDeque<Integer> yOfOverloadedCells = new ArrayDeque<Integer>();

        // moves units to their destination blindly
        for (Action move : moves) {
            Unit unit = map.getCellAtPoint(move.getPosition()).getUnit();
            if (unit == null)
                continue;
            Cell source = unit.getCell();
            Cell destination = map.getNeighborCell(source, move.getDirection());
            if (tempOtherMoves[source.getX()][source.getY()] != null && tempOtherMoves[source.getX()][source.getY()].size() > 0 &&
                    destination.getType() != CellType.MOUNTAIN && destination.getType() != CellType.RIVER &&
                    destination.getType() != CellType.OUTOFMAP &&
                    source.getEdge(move.getDirection()).getType() == EdgeType.OPEN && !destination.equals(map.getDestinationCell((unit.getTeamId() + 1) % 2)) &&
                    !destination.equals(map.getSpawnCell((unit.getTeamId() + 1) % 2))) {

                tempOtherMoves[source.getX()][source.getY()].remove(0);
                tempOtherMoves[destination.getX()][destination.getY()].add(unit);
            }
        }

        // find cells with multiple units inside
        for (int i = 0; i < tempOtherMoves.length; i++)
            for (int j = 0; j < tempOtherMoves[0].length; j++)
                if (tempOtherMoves[i][j].size() > 1) {
                    xOfOverloadedCells.add(i);
                    yOfOverloadedCells.add(j);
                }

        Random rand = new Random();
        while (!xOfOverloadedCells.isEmpty()) {
            int xTemp = xOfOverloadedCells.pop();
            int yTemp = yOfOverloadedCells.pop();
            int overloadedNumber = tempOtherMoves[xTemp][yTemp].size();

            if (overloadedNumber < 2)
                continue;

            // checks if a unit stays and some other want to move to its cell
            boolean isDestinationFull = false;
            int stayerId = -1;
            int zombieNum = 0;
            for (int i = 0; i < overloadedNumber; i++) {
                if (tempOtherMoves[xTemp][yTemp].get(i).getTeamId() == 1)
                    zombieNum++;
                Unit existent = tempOtherMoves[xTemp][yTemp].get(i);
                if (existent.getCell().getX() == xTemp && existent.getCell().getY() == yTemp) {
                    isDestinationFull = true;
                    stayerId = existent.getId();
                }
            }

            if (!isDestinationFull) {
                // only move "lasting" unit and others must stay
                int lasting = 0;
                if (zombieNum > 0) {
                    int zombieLasting = rand.nextInt(zombieNum);
                    for (int i = 0; i < overloadedNumber; i++) {
                        if (zombieLasting == 0 && tempOtherMoves[xTemp][yTemp].get(i).getTeamId() == 1) {
                            lasting = i;
                            zombieLasting--;
                        }
                        else if (tempOtherMoves[xTemp][yTemp].get(i).getTeamId() == 1)
                            zombieLasting--;
                    }
                } else {
                    lasting = rand.nextInt(overloadedNumber);
                }
                for (int i = overloadedNumber - 1; i >= 0; i--)
                    if (i != lasting) {
                        Unit goner = tempOtherMoves[xTemp][yTemp].get(i);
                        tempOtherMoves[goner.getCell().getX()][goner.getCell().getY()].add(goner);

                        // if some other unit wanted to move to previous location of this unit, they must go back
                        if (tempOtherMoves[goner.getCell().getX()][goner.getCell().getY()].size() > 1) {
                            xOfOverloadedCells.add(goner.getCell().getX());
                            yOfOverloadedCells.add(goner.getCell().getY());
                        }
                        tempOtherMoves[xTemp][yTemp].remove(i);
                    }
            } else {
                for (int i = overloadedNumber - 1; i >= 0; i--) {
                    Unit goner = tempOtherMoves[xTemp][yTemp].get(i);
                    // send everybody back, except the one who stayed in the cell
                    if (goner.getId() != stayerId) {
                        tempOtherMoves[goner.getCell().getX()][goner.getCell().getY()].add(goner);
                        if (tempOtherMoves[goner.getCell().getX()][goner.getCell().getY()].size() > 1) {
                            xOfOverloadedCells.add(goner.getCell().getX());
                            yOfOverloadedCells.add(goner.getCell().getY());
                        }
                        tempOtherMoves[xTemp][yTemp].remove(i);
                    }
                }
            }
        }

        for (int i = 0; i < tempOtherMoves.length; i++)
            for (int j = 0; j < tempOtherMoves[0].length; j++) {
                if (tempOtherMoves[i][j].size() == 0)
                    continue;
                Unit thisUnit = tempOtherMoves[i][j].get(0);
                Cell tempCell = thisUnit.getCell();
                Point sourcePoint = new Point (tempCell.getX(), tempCell.getY());
                // if this unit is moved, make delta
                if (thisUnit.getCell().getX() != i || thisUnit.getCell().getY() != j) {
                    Point destinationPoint = new Point(i, j);
                    moveDeltas.add(new Delta(DeltaType.CELL_MOVE, sourcePoint, destinationPoint));
                    if (destinationPoint.equals(map.getDestinationPoint(thisUnit.getTeamId()))) {
                        if (thisUnit.getTeamId() == 0)
                            CEScore += 5;
                        otherDeltas.add(new Delta(DeltaType.AGENT_DISAPPEAR, destinationPoint));
                        CETeam.increaseArrivedNumber();
                    }
                // if this unit is stayed in mine
                } else if (thisUnit.getCell().getX() == i && thisUnit.getCell().getY() == j &&
                        thisUnit.getCell().getType() == CellType.MINE && thisUnit.getTeamId() == 0) {
                    MineCell mineCell = (MineCell) thisUnit.getCell();
                    if (mineCell.getAmount() >= MINE_RATE) {
                        resources[thisUnit.getTeamId()] += MINE_RATE;
                        CETeam.increaseResources(MINE_RATE);
                        otherDeltas.add(new Delta(DeltaType.MINE_CHANGE, sourcePoint, MINE_RATE));
                        otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, thisUnit.getTeamId(), MINE_RATE));
                    } else if (mineCell.getAmount() > 0) {
                        resources[thisUnit.getTeamId()] += mineCell.getAmount();
                        CETeam.increaseResources(mineCell.getAmount());
                        otherDeltas.add(new Delta(DeltaType.MINE_CHANGE, sourcePoint, mineCell.getAmount()));
                        otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, thisUnit.getTeamId(), mineCell.getAmount()));
                    }
                }
            }
    }

    private boolean isTherePathAfterThisEdges (Point sourceInput, Point destinationInput, ArrayList<Edge> barriers) {
        Cell source = map.getCellAt(sourceInput.getX(), sourceInput.getY());
        Cell destination = map. getCellAt(destinationInput.getX(), destinationInput.getY());
        boolean[][] flags = new boolean[map.getSizeX()][map.getSizeY()];
        Cell currentCell;
        Stack<Cell> dfs = new Stack<Cell>();
        dfs.add(source);
        Direction[] dir = Direction.values();
        while (!dfs.isEmpty()) {
            currentCell = dfs.pop();
            if (currentCell.equals(destination))
                return true;
            flags[currentCell.getX()][currentCell.getY()] = true;
            outer: for (int i = 0; i < 6; i++) {
                Cell neighborCell = map.getNeighborCell(currentCell, dir[i]);
                Edge neighborEdge = currentCell.getEdge(dir[i]);
                if (neighborCell != null && flags[neighborCell.getX()][neighborCell.getY()] == false &&
                        neighborEdge.getType() == EdgeType.OPEN &&
                        (neighborCell.getType() == CellType.TERRAIN ||
                        neighborCell.getType() == CellType.MINE ||
                        neighborCell.getType() == CellType.SPAWN ||
                        neighborCell.getType() == CellType.DESTINATION)) {
                    for (int j = 0; j < barriers.size(); j++)
                        if (neighborEdge.equals(barriers.get(j)))
                            continue outer;
                    dfs.add(neighborCell);
                }
            }
        }
        return false;
    }

    public void initTurn (int turn) {
        //attackDeltas = new ArrayList<Delta>();
        wallDeltas = new ArrayList<Delta>();
        moveDeltas = new ArrayList<Delta>();
        otherDeltas = new ArrayList<Delta>();
        tempOtherMoves = new ArrayList[map.getSizeX()][map.getSizeY()];
        for (int i = 0; i < map.getSizeX(); i++)
            for(int j = 0; j < map.getSizeY(); j++) {
                tempOtherMoves[i][j] = new ArrayList<Unit>();
                if (map.getCellAt(i, j).getUnit() != null)
                    tempOtherMoves[i][j].add(map.getCellAt(i, j).getUnit());
            }

        this.turn = turn;
        if (turn == GAME_LENGTH) {
            ended = true;
        }
    }

    public void endTurn() {
        if (turn % EE_SPAWN_RATE == 0) {
            if (map.getCellAtPoint(map.getSpawnPoint(1)).getUnit() == null) {
                Unit newUnit = EETeam.addUnit();
                System.out.println("[EE] Generating a SpawnDelta with id = " + newUnit.getId());
                otherDeltas.add(new Delta(DeltaType.SPAWN, map.getSpawnPoint(1), 1, newUnit.getId()));
            }
        }
        if (turn % CE_SPAWN_RATE == 0) {
            if (map.getCellAtPoint(map.getSpawnPoint(0)).getUnit() == null) {
                Unit newUnit = CETeam.addUnit();
                System.out.println("[CE] Generating a SpawnDelta with id = " + newUnit.getId());
                otherDeltas.add(new Delta(DeltaType.SPAWN, map.getSpawnPoint(0), 0, newUnit.getId()));
            }
        }
    }

    public ArrayList<Delta> getWallDeltasList() {
        return wallDeltas;
    }

    public ArrayList<Delta> getMoveDeltasList() {
        return moveDeltas;
    }

    public ArrayList<Delta> getOtherDeltasList() {
        return otherDeltas;
    }

    public int getCEScore() {
        return CEScore;
    }
}
