package javachallenge.server;

import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.message.Action;
import javachallenge.message.Delta;
import javachallenge.message.DeltaType;
import javachallenge.util.*;
import javachallenge.util.Map;

import java.util.*;

public class Game {

    private static final int MINE_RATE = 4;                 // resource per turn
    private static final int COST_MAKE_WALL = 10;
    private static final int COST_DESTROY_WALL = 6;
    private static final int GAME_LENGTH = 50;             // turn
    private static final int UNIT_SPAWN_RATE = 2;           // each 2 turn
    private static final int WALLS_MADE_PER_TURN = 3;
    private static final int WALLS_DESTROYED_PER_TURN = 2;
    public static final int INITIAL_RESOURCE = 150;         // each team's initial resource

    private static final int UNIT_ARRIVAL_SCORE = 5;

    private Team[] teams = new Team[2];

    private Map map;

    private boolean ended;

    private ArrayList<Delta> wallDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> moveDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> attackDeltas = new ArrayList<Delta>();
    private ArrayList<Delta> otherDeltas = new ArrayList<Delta>();

    private ArrayList<Point> updatedPoints = new ArrayList<Point>();

    private int turn;
    private int winner;

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
        ArrayList<Action> attackActions = new ArrayList<Action>();
        ArrayList<Action> moveActions = new ArrayList<Action>();
        ArrayList<Action> makeWallActions = new ArrayList<Action>();
        ArrayList<Action> destroyWallActions = new ArrayList<Action>();
        if (actions == null)
            return;
        for (Action action : actions) {
            if (action.isValid())
                switch (action.getType()) {
                    case MOVE:
                        if (actionIsValid(action))
                            moveActions.add(action);
                        break;
                    case MAKE_WALL:
                        makeWallActions.add(action);
                        break;
                    case DESTROY_WALL:
                        destroyWallActions.add(action);
                        break;
                    case ATTACK:
                        if (actionIsValid(action))
                            attackActions.add(action);
                        break;
                }
        }
        handleAttacks(attackActions);
        map.updateMap(this.getAttackDeltas());
        handleMoveActions(moveActions);
        map.updateMap(this.getMoveDeltasList());
        handleWallActions(makeWallActions, destroyWallActions);
        map.updateMap(this.getWallDeltasList());
    }

    private void handleAttacks(ArrayList<Action> attacks) {
        for (Action attack: attacks) {
            try {
                Cell cell = map.getCellAtPoint(attack.getPosition());
                Cell neighborCell = map.getNeighborCell(cell, attack.getDirection());
                if (neighborCell.isGround()) {
                    attackDeltas.add(new Delta(DeltaType.AGENT_ATTACK, cell.getPoint(), attack.getDirection()));
                    if(neighborCell.getUnit() != null && neighborCell.getUnit().getTeamId() != cell.getUnit().getTeamId())
                        attackDeltas.add(new Delta(DeltaType.AGENT_KILL, neighborCell.getPoint()));
                }
            } catch (CellIsNullException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleWallActions(ArrayList<Action> makeWalls, ArrayList<Action> destroyWalls) {
        int[] makeWallsNumber = new int[2];
        int[] destroyWallsNumber = new int[2];

        Collections.shuffle(makeWalls);
        Collections.shuffle(destroyWalls);

        ArrayList<Edge> wallsToBe = new ArrayList<Edge>();

        for (Action makeWallAction : makeWalls) {
            try {
                Cell cell = map.getCellAtPoint(makeWallAction.getPosition());
                Edge edge = cell.getEdge(makeWallAction.getDirection());

                Team team = getTeam(makeWallAction.getTeamId());

                if (team.getResource() >= COST_MAKE_WALL && edge.getType() == EdgeType.OPEN &&
                        makeWallsNumber[team.getTeamId()] < WALLS_MADE_PER_TURN &&
                        !wallsToBe.contains(edge)) {

                    wallsToBe.add(edge);


                    team.decreaseResources(COST_MAKE_WALL);

                    makeWallsNumber[team.getTeamId()]++;

                    wallDeltas.add(new Delta(DeltaType.WALL_MAKE, makeWallAction.getPosition(), makeWallAction.getDirection()));
                    otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, team.getTeamId(), -COST_MAKE_WALL));
                }
            } catch (CellIsNullException e) {
                e.printStackTrace();
            }
        }
        for (Action destroyWallAction : destroyWalls) {
            try {
                Cell cell = map.getCellAtPoint(destroyWallAction.getPosition());
                Edge edge = cell.getEdge(destroyWallAction.getDirection());

                Team team = getTeam(destroyWallAction.getTeamId());

                if (team.getResource() >= COST_DESTROY_WALL && edge.getType() == EdgeType.WALL &&
                        destroyWallsNumber[team.getTeamId()] < WALLS_DESTROYED_PER_TURN &&
                        !wallsToBe.contains(edge)) {

                    wallsToBe.add(edge);

                    team.decreaseResources(COST_DESTROY_WALL);

                    destroyWallsNumber[team.getTeamId()]++;

                    wallDeltas.add(new Delta(DeltaType.WALL_DESTROY, destroyWallAction.getPosition(), destroyWallAction.getDirection()));
                    otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, team.getTeamId(), -COST_DESTROY_WALL));
                }
            } catch (CellIsNullException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleMoveActions(ArrayList<Action> moves) {

        ArrayList<Unit>[][] future = new ArrayList[map.getSizeX()][map.getSizeY()];

        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++) {
                future[i][j] = new ArrayList<Unit>();
                try {
                    if (map.getCellAt(i, j).getUnit() != null)
                        future[i][j].add(map.getCellAt(i, j).getUnit());
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }

        ArrayDeque<Point> overloadedCells = new ArrayDeque<Point>();

        // moves units to their destination blindly
        for (Action moveAction : moves) {
            try {
                Cell source = map.getCellAtPoint(moveAction.getPosition());
                Unit unit = source.getUnit();
                Cell destination = map.getNeighborCell(source, moveAction.getDirection());

                if (future[source.getX()][source.getY()].size() > 0 &&
                        destination.isGround() &&
                        source.getEdge(moveAction.getDirection()).getType() == EdgeType.OPEN) {

                    future[source.getX()][source.getY()].remove(0);
                    future[destination.getX()][destination.getY()].add(unit);
                }
            } catch (CellIsNullException e) {
                e.printStackTrace();
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
            Unit stayer = null;
            for (int i = 0; i < overloadedNumber; i++) {
                Unit existent = future[temp.getX()][temp.getY()].get(i);
                if (existent.getCell().getPoint().equals(temp)) {
                    isDestinationFull = true;
                    stayer = existent;
                }
            }

            if (!isDestinationFull) {
                // only move "lasting" unit and others must stay
                int lasting = rand.nextInt(overloadedNumber);
                for (int i = overloadedNumber - 1; i >= 0; i--)
                    if (i != lasting) {
                        Unit loser = future[temp.getX()][temp.getY()].get(i);
                        future[loser.getCell().getX()][loser.getCell().getY()].add(loser);

                        // if some other unit wanted to move to previous location of this unit, they must go back
                        if (future[loser.getCell().getX()][loser.getCell().getY()].size() > 1) {
                            overloadedCells.add(loser.getCell().getPoint());
                        }
                        future[temp.getX()][temp.getY()].remove(i);
                    }
            } else {
                for (int i = overloadedNumber - 1; i >= 0; i--) {
                    Unit loser = future[temp.getX()][temp.getY()].get(i);

                    // send everybody back, except the one who stayed in the cell
                    if (loser.getId() != stayer.getId()) {
                        future[loser.getCell().getX()][loser.getCell().getY()].add(loser);
                        if (future[loser.getCell().getX()][loser.getCell().getY()].size() > 1) {
                            overloadedCells.add(loser.getCell().getPoint());
                        }
                        future[temp.getX()][temp.getY()].remove(i);
                    }
                }
            }
        }

        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++) {
                if (future[i][j].size() == 0)
                    continue;

                Unit unit = future[i][j].get(0);
                Cell unitCell = unit.getCell();
                Point sourcePoint = unitCell.getPoint();
                Point here = new Point(i, j);

                // if this unit is moved, make delta
                if (!sourcePoint.equals(here)) {
                    try {
                        moveDeltas.add(new Delta(
                                DeltaType.CELL_MOVE,
                                sourcePoint,
                                map.getDirectionFromTwoPoints(sourcePoint, here))
                        );
                    } catch (CellIsNullException e) {
                        e.printStackTrace();
                    }

                    if (map.getDestinationPoint(unit.getTeamId()).equals(here)) {
                        getTeam(unit.getTeamId()).updateScore(UNIT_ARRIVAL_SCORE);
                        otherDeltas.add(new Delta(DeltaType.AGENT_ARRIVE, here));
                        getTeam(unit.getTeamId()).increaseArrivedNumber();
                    }

                // if this unit is stayed in mine
                } else try {
                    if (sourcePoint.equals(here) &&
                            map.getCellAtPoint(sourcePoint).getType() == CellType.MINE) {

                        MineCell mineCell = (MineCell) map.getCellAtPoint(sourcePoint);
                        if (mineCell.getAmount() > MINE_RATE) {
                            getTeam(unit.getTeamId()).increaseResources(MINE_RATE);

                            otherDeltas.add(new Delta(DeltaType.MINE_CHANGE, sourcePoint, -MINE_RATE));
                            otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, unit.getTeamId(), MINE_RATE));

                        } else if (mineCell.getAmount() > 0) {
                            getTeam(unit.getTeamId()).increaseResources(mineCell.getAmount());
                            updatedPoints.add(mineCell.getPoint());

                            otherDeltas.add(new Delta(DeltaType.MINE_CHANGE, sourcePoint, -mineCell.getAmount()));
                            otherDeltas.add(new Delta(DeltaType.RESOURCE_CHANGE, unit.getTeamId(), mineCell.getAmount()));
                            otherDeltas.add(new Delta(DeltaType.MINE_DISAPPEAR, sourcePoint));
                        }
                    }
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
    }

    public void initTurn(int turn) {
        attackDeltas = new ArrayList<Delta>();
        wallDeltas = new ArrayList<Delta>();
        moveDeltas = new ArrayList<Delta>();
        otherDeltas = new ArrayList<Delta>();

        updatedPoints.clear();

        this.turn = turn;
        if (turn == GAME_LENGTH) {
            ended = true;
            if (teams[0].getArrivedUnitsNum() > teams[1].getArrivedUnitsNum())
                winner = 0;
            else if (teams[0].getArrivedUnitsNum() < teams[1].getArrivedUnitsNum())
                winner = 1;
            else
                if (teams[0].getResource() > teams[1].getResource())
                    winner = 0;
                else if (teams[0].getResource() < teams[1].getResource())
                    winner = 1;
            System.out.println("Winner is: " + teams[winner].getName());
        }
    }

    public void handleSpawns() {
        if (turn % UNIT_SPAWN_RATE == 1) {
            for (Team team : teams) {
                try {
                    if (map.getCellAtPoint(map.getSpawnPoint(team.getTeamId())).getUnit() == null) {
                        Unit newUnit = team.addUnit();
                        otherDeltas.add(new Delta(DeltaType.SPAWN,
                                map.getSpawnPoint(team.getTeamId()),
                                team.getTeamId(),
                                newUnit.getId())
                        );

                        System.out.println("[" + team.getName() + "] Spawning a unit with id = " + newUnit.getId());
                    }
                } catch (CellIsNullException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean actionIsValid(Action action) {
        try {
            if (map.isCellInMap(action.getPosition())) {
                Unit unit = map.getCellAtPoint(action.getPosition()).getUnit();
                return unit != null && unit.getTeamId() == action.getTeamId();
            } else {
                return false;
            }
        } catch (CellIsNullException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void endTurn() {
        handleSpawns();
    }

    public ArrayList<Delta> getAttackDeltas() {
        return attackDeltas;
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

    public ArrayList<Point> getUpdatedPoints() {
        return updatedPoints;
    }
}
