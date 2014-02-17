package javachallenge.server;

import javachallenge.units.Unit;
import javachallenge.message.Action;
import javachallenge.message.ActionType;
import javachallenge.message.Delta;
import javachallenge.message.DeltaType;
import javachallenge.util.*;
import javachallenge.util.Map;

import java.util.*;

public class Game {

    private static final int MINE_RATE = 4;                 // resource per turn
    private static final int COST_WALL = 10;
    private static final int GAME_LENGTH = 700;             // turn
    private static final int UNIT_SPAWN_RATE = 2;           // each 2 turn
    public static final int INITIAL_RESOURCE = 150;         // each team's initial resource

    public static final int WALL_SCORE = -3;

    private Team[] teams = new Team[2];

    private Map map;

    private boolean ended;

    private ArrayList<Delta> wallDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> moveDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> otherDeltas = new ArrayList<Delta>();

    private int turn;

    public int getTurn() {
        return turn;
    }

    public Team getTeam(int teamId) {
        return teams[teamId];
    }

    public boolean isEnded() {
        return ended;
    }

    public Game(Map map) {
        this.map = map;
    }

    public void addTeam(Team team) {
        if (teams[0] == null) {
            teams[0] = team;
            teams[0].setTeamId(0);
        } else {
            teams[1] = team;
            teams[1].setTeamId(1);
        }
    }

    public Map getMap() {
        return map;
    }

    public void handleActions(ArrayList<Action> actions) {
        ArrayList<Action> moveActions = new ArrayList<Action>();
        ArrayList<Action> wallActions = new ArrayList<Action>();
        for (Action action : actions) {
            switch (action.getType()) {
                case MOVE:
                    if (action.getPosition() != null && action.getDirection() != null)
                        moveActions.add(action);
                    break;
                case MAKE_WALL:
                    if (action.getPosition() != null && action.getNodeDirection() != null)
                        wallActions.add(action);
                    break;
            }
        }

        handleWallActions(wallActions);
        map.updateMap(this.getWallDeltasList());
        handleMoveActions(moveActions);
        map.updateMap(this.getMoveDeltasList());
    }

    public void handleWallActions(ArrayList<Action> walls) {
        Collections.shuffle(walls);
        ArrayList<Edge> wallsToBeMade = new ArrayList<Edge>();

        for (Action wallAction : walls) {
            if (!map.isNodeInMap(wallAction.getPosition()))
                continue;

            Node node1 = map.getNodeAtPoint(wallAction.getPosition());
            Node node2 = map.getNeighborNode(node1, wallAction.getNodeDirection());

            if (node2 == null)
                continue;

            Edge edge = node1.getEdge(wallAction.getNodeDirection());
            Team team = getTeam(wallAction.getTeamId());

            if (team.getResource() >= COST_WALL &&
                    wallAction.getType() == ActionType.MAKE_WALL &&
                    edge.getType() == EdgeType.OPEN) {

                wallsToBeMade.add(edge);

                if (isTherePathAfterThisEdges(map.getSpawnPoint(0), map.getDestinationPoint(0), wallsToBeMade) &&
                        isTherePathAfterThisEdges(map.getSpawnPoint(1), map.getDestinationPoint(1), wallsToBeMade)) {

                    team.decreaseResources(COST_WALL);
                    team.updateScore(-WALL_SCORE);
                    wallDeltas.add(new Delta(DeltaType.WALL_DRAW, wallAction.getPosition(), node2.getPoint()));
                    otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, 0, -COST_WALL));
                } else
                    wallsToBeMade.remove(edge);
            }
        }
    }

    @SuppressWarnings("unchecked")

    private void handleMoveActions(ArrayList<Action> moves) {

        ArrayList<Unit>[][] future = new ArrayList[map.getSizeX()][map.getSizeY()];

        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++) {
                future[i][j] = new ArrayList<Unit>();
                if (map.getCellAt(i, j).getUnit() != null)
                    future[i][j].add(map.getCellAt(i, j).getUnit());
            }

        ArrayDeque<Integer> xOfOverloadedCells = new ArrayDeque<Integer>();
        ArrayDeque<Integer> yOfOverloadedCells = new ArrayDeque<Integer>();

        ArrayDeque<Point> overloadedCells = new ArrayDeque<Point>();

        // moves units to their destination blindly
        for (Action moveAction : moves) {

            if (!map.isCellInMap(moveAction.getPosition()))
                continue;

            Cell source = map.getCellAtPoint(moveAction.getPosition());
            Unit unit = source.getUnit();
            Cell destination = map.getNeighborCell(source, moveAction.getDirection());

            if (unit == null || destination == null) {
                continue;
            }

            if (future[source.getX()][source.getY()].size() > 0 &&
                    destination.isGround() &&
                    source.getEdge(moveAction.getDirection()).getType() == EdgeType.OPEN) { //&&
                    //!destination.equals(map.getDestinationCell((unit.getTeamId() + 1) % 2)) &&
                    //!destination.equals(map.getSpawnCell((unit.getTeamId() + 1) % 2))) {

                future[source.getX()][source.getY()].remove(0);
                future[destination.getX()][destination.getY()].add(unit);
            }
        }

        // find cells with multiple units inside
        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++)
                if (future[i][j].size() > 1)
                    overloadedCells.add(new Point(i, j));

        Random rand = new Random();
        while (!overloadedCells.isEmpty()) {
            Point temp = overloadedCells.pop();
            int overloadedNumber = future[temp.getX()][temp.getY()].size();

            if (overloadedNumber < 2)
                continue;

            // checks if a unit stays and some other want to move to its cell
            boolean isDestinationFull = false;
            int stayerId = -1;
            int zombieNum = 0;
            for (int i = 0; i < overloadedNumber; i++) {
                Unit existent = future[temp.getX()][temp.getY()].get(i);
                if (existent.getTeamId() == 1) {
                    zombieNum++;
                }
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
                        if (zombieLasting == 0 && future[xTemp][yTemp].get(i).getTeamId() == 1) {
                            lasting = i;
                            zombieLasting--;
                        } else if (future[xTemp][yTemp].get(i).getTeamId() == 1)
                            zombieLasting--;
                    }
                } else {
                    lasting = rand.nextInt(overloadedNumber);
                }
                for (int i = overloadedNumber - 1; i >= 0; i--)
                    if (i != lasting) {
                        Unit goner = future[xTemp][yTemp].get(i);
                        future[goner.getCell().getX()][goner.getCell().getY()].add(goner);

                        // if some other unit wanted to move to previous location of this unit, they must go back
                        if (future[goner.getCell().getX()][goner.getCell().getY()].size() > 1) {
                            xOfOverloadedCells.add(goner.getCell().getX());
                            yOfOverloadedCells.add(goner.getCell().getY());
                        }
                        future[xTemp][yTemp].remove(i);
                    }
            } else {
                for (int i = overloadedNumber - 1; i >= 0; i--) {
                    Unit goner = future[xTemp][yTemp].get(i);
                    // send everybody back, except the one who stayed in the cell
                    if (goner.getId() != stayerId) {
                        future[goner.getCell().getX()][goner.getCell().getY()].add(goner);
                        if (future[goner.getCell().getX()][goner.getCell().getY()].size() > 1) {
                            xOfOverloadedCells.add(goner.getCell().getX());
                            yOfOverloadedCells.add(goner.getCell().getY());
                        }
                        future[xTemp][yTemp].remove(i);
                    }
                }
            }
        }

        for (int i = 0; i < future.length; i++)
            for (int j = 0; j < future[0].length; j++) {
                if (future[i][j].size() == 0)
                    continue;
                Unit thisUnit = future[i][j].get(0);
                Cell tempCell = thisUnit.getCell();
                Point sourcePoint = new Point(tempCell.getX(), tempCell.getY());
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

    private boolean isTherePathAfterThisEdges(Point sourceInput, Point destinationInput, ArrayList<Edge> barriers) {
        Cell source = map.getCellAt(sourceInput.getX(), sourceInput.getY());
        Cell destination = map.getCellAt(destinationInput.getX(), destinationInput.getY());
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
            outer:
            for (int i = 0; i < 6; i++) {
                Cell neighborCell = map.getNeighborCell(currentCell, dir[i]);
                Edge neighborEdge = currentCell.getEdge(dir[i]);
                if (neighborCell != null && !flags[neighborCell.getX()][neighborCell.getY()] &&
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

    public void initTurn(int turn) {
        //attackDeltas = new ArrayList<Delta>();
        wallDeltas = new ArrayList<Delta>();
        moveDeltas = new ArrayList<Delta>();
        otherDeltas = new ArrayList<Delta>();

        this.turn = turn;
        if (turn == GAME_LENGTH) {
            ended = true;
        }
    }

    public void endTurn() {
//        if (turn % EE_SPAWN_RATE == 0) {
//            if (map.getCellAtPoint(map.getSpawnPoint(1)).getUnit() == null) {
//                Unit newUnit = EETeam.addUnit();
//                System.out.println("[EE] Generating a SpawnDelta with id = " + newUnit.getId());
//                otherDeltas.add(new Delta(DeltaType.SPAWN, map.getSpawnPoint(1), 1, newUnit.getId()));
//            }
//        }
        if (turn % UNIT_SPAWN_RATE == 0) {
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
