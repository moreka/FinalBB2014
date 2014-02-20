package javachallenge.util;

import javachallenge.exceptions.CellIsNullException;
import javachallenge.mapParser.Parser;
import javachallenge.message.Delta;
import javachallenge.units.Unit;

import java.io.*;
import java.util.ArrayList;

public class Map implements Serializable, Cloneable {
    private Cell[][] cells;
    private ArrayList<MineCell> mines;
    private int sizeX;
    private int sizeY;
    private int MINE_AMOUNT;
    private Point[] spawnPoints = new Point[2];
    private Point[] destinationPoints = new Point[2];
    private String string;

    public Map(MapHelper mapHelper) {
        sizeX = mapHelper.getSizeX();
        sizeY = mapHelper.getSizeY();
        this.cells = new Cell[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++)
            for (int j = 0; j < cells[i].length; j++)
                cells[i][j].setType(mapHelper.getCells()[i][j]);
        spawnPoints[0] = mapHelper.getSpawn1();
        destinationPoints[0] = mapHelper.getDestination1();
        spawnPoints[1] = mapHelper.getSpawn2();
        destinationPoints[1] = mapHelper.getDestination2();
        mines = new ArrayList<MineCell>();
    }

    public void createJSON() {
        MapHelper mapHelper = new MapHelper(this);
        Parser parser = new Parser();
        try {
            parser.javaToJson(mapHelper, "net.mapHelper");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Point getSpawnPoint(int teamId) {
        return spawnPoints[teamId];
    }

    public void setSpawnPoints(Point[] spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public void setDestinationPoints(Point[] destinationPoints) {
        this.destinationPoints = destinationPoints;
    }

    public Cell getSpawnCell(int teamId) throws CellIsNullException {
        return getCellAtPoint(getSpawnPoint(teamId));
    }

    public Point getDestinationPoint(int teamId) {
        return destinationPoints[teamId];
    }

    public Cell getDestinationCell(int teamId) throws CellIsNullException {
        return getCellAtPoint(getDestinationPoint(teamId));
    }

    public Map(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.cells = new Cell[sizeX][sizeY];
        this.mines = new ArrayList<MineCell>();
    }

    private void init() {

        for (int i = 0; i < this.sizeX; i++) {
            for (int j = 0; j < this.sizeY; j++) {
                if (this.cells[i][j].getType().equals(CellType.OUTOFMAP) || this.cells[i][j].getType().equals(CellType.MOUNTAIN) || this.cells[i][j].getType().equals(CellType.RIVER))
                    continue;
                for (Direction d : Direction.values()) {
                    Cell[] input = new Cell[2];
                    input[0] = cells[i][j];
                    this.cells[i][j].getEdge(d).setType(EdgeType.OPEN);
                    try {
                        input[1] = getNeighborCell(cells[i][j], d);
                        input[1].setEdge(this.cells[i][j].getEdge(d), Direction.values()[(d.ordinal() + 3) % 6]);
                    } catch (CellIsNullException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static Map loadMap(String mapAddr) throws IOException, IndexOutOfBoundsException {
        BufferedReader br = new BufferedReader(new FileReader(mapAddr));

        String str = "";
        Map map = new Map(Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()));
        str = map.getSizeX() + "\n" + map.getSizeY();
        for (int i = 0; i < map.getSizeY(); i++) {
            String line = br.readLine();
            str = str + "\n" + line;
            String[] cell_types = line.split("[ ]");
            for (int j = 0; j < map.getSizeX(); j++) {
                int index = Integer.parseInt(cell_types[j]);
                if (index == CellType.MINE.ordinal()) {
                    map.cells[j][i] = new MineCell(j, i);
                    map.mines.add((MineCell) map.cells[j][i]);
                } else {
                    map.cells[j][i] = new Cell(j, i, CellType.values()[Integer.parseInt(cell_types[j])]);
                }
            }
        }
        // System.out.println(br.readLine());
        map.MINE_AMOUNT = Integer.parseInt(br.readLine());
        str = str + "\n" + map.MINE_AMOUNT;
        for (MineCell mine : map.getMines())
            mine.setAmount(map.MINE_AMOUNT);

        String line1 = br.readLine(), line2 = br.readLine();
        str += "\n" + line1 + "\n" + line2;
        map.setSpawnPoints(new Point[]{
                new Point(Integer.parseInt(line1.split("[ ]")[0]),
                        Integer.parseInt(line1.split("[ ]")[1])),
                new Point(Integer.parseInt(line2.split("[ ]")[0]),
                        Integer.parseInt(line2.split("[ ]")[1]))
        });
        line1 = br.readLine();
        line2 = br.readLine();
        str += "\n" + line1 + "\n" + line2;
        map.setDestinationPoints(new Point[]{
                new Point(Integer.parseInt(line1.split("[ ]")[0]),
                        Integer.parseInt(line1.split("[ ]")[1])),
                new Point(Integer.parseInt(line2.split("[ ]")[0]),
                        Integer.parseInt(line2.split("[ ]")[1]))
        });

        map.setString(str);
        map.init();
        return map;
    }

    public static Map loadMapFromString(String mapString) throws IndexOutOfBoundsException, IOException {
        BufferedReader br = new BufferedReader(new StringReader(mapString));
        Map map = new Map(Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()));
        String str = "";
        str = map.getSizeX() + "\n" + map.getSizeY();
        for (int i = 0; i < map.getSizeY(); i++) {
            String line = br.readLine();
            str = str + "\n" + line;
            String[] cell_types = line.split("[ ]");
            for (int j = 0; j < map.getSizeX(); j++) {
                int index = Integer.parseInt(cell_types[j]);
                if (index == CellType.MINE.ordinal()) {
                    map.cells[j][i] = new MineCell(j, i);
                    map.mines.add((MineCell) map.cells[j][i]);
                } else {
                    map.cells[j][i] = new Cell(j, i, CellType.values()[Integer.parseInt(cell_types[j])]);
                }
            }
        }
        map.MINE_AMOUNT = Integer.parseInt(br.readLine());
        for (MineCell mine : map.getMines())
            mine.setAmount(map.MINE_AMOUNT);
        String line1 = br.readLine(), line2 = br.readLine();
        str += "\n" + line1 + "\n" + line2;
        map.setSpawnPoints(new Point[]{
                new Point(Integer.parseInt(line1.split("[ ]")[0]),
                        Integer.parseInt(line1.split("[ ]")[1])),
                new Point(Integer.parseInt(line2.split("[ ]")[0]),
                        Integer.parseInt(line2.split("[ ]")[1]))
        });
        line1 = br.readLine();
        line2 = br.readLine();
        str += "\n" + line1 + "\n" + line2;
        map.setDestinationPoints(new Point[]{
                new Point(Integer.parseInt(line1.split("[ ]")[0]),
                        Integer.parseInt(line1.split("[ ]")[1])),
                new Point(Integer.parseInt(line2.split("[ ]")[0]),
                        Integer.parseInt(line2.split("[ ]")[1]))
        });
        map.setString(str);
        map.init();
        return map;
    }

    public ArrayList<MineCell> getMines() {
        return mines;
    }

    public boolean isCellInMap(int x, int y) {
        if (x >= 0 && x < sizeX && y >= 0 && y < sizeY)
            return true;
        return false;
    }

    public Cell getCellAt(int x, int y) throws CellIsNullException {
        if (isCellInMap(x, y))
            return cells[x][y];
        throw new CellIsNullException();
    }

    public Cell getCellAtPoint(Point point) throws CellIsNullException {
        int x = point.getX();
        int y = point.getY();
        if (isCellInMap(x, y))
            return cells[x][y];
        throw new CellIsNullException();
    }

    public Cell getNeighborCell(Cell c, Direction dir) throws CellIsNullException {
        int x = 0, y = 0;
        switch (dir) {
            case EAST:
                x = c.getX() + 1;
                y = c.getY();
                break;
            case WEST:
                x = c.getX() - 1;
                y = c.getY();
                break;
            default:
                if (c.getY() % 2 == 1) {
                    switch (dir) {
                        case NORTHEAST:
                            x = c.getX() + 1;
                            y = c.getY() - 1;
                            break;
                        case SOUTHEAST:
                            x = c.getX() + 1;
                            y = c.getY() + 1;
                            break;
                        case NORTHWEST:
                            x = c.getX();
                            y = c.getY() - 1;
                            break;
                        case SOUTHWEST:
                            x = c.getX();
                            y = c.getY() + 1;
                            break;
                    }
                } else {
                    switch (dir) {
                        case NORTHWEST:
                            x = c.getX() - 1;
                            y = c.getY() - 1;
                            break;
                        case SOUTHEAST:
                            x = c.getX();
                            y = c.getY() + 1;
                            break;
                        case NORTHEAST:
                            x = c.getX();
                            y = c.getY() - 1;
                            break;
                        case SOUTHWEST:
                            x = c.getX() - 1;
                            y = c.getY() + 1;
                            break;
                    }
                }
        }

        if (isCellInMap(x, y))
            return cells[x][y];

        throw new CellIsNullException();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Unit>[][] initUpdate() {
        ArrayList<Unit>[][] units = new ArrayList[sizeX][sizeY];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (cells[i][j].getUnit() != null) {
                    if (units[i][j] == null)
                        units[i][j] = new ArrayList<Unit>(2);
                    units[i][j].add(cells[i][j].getUnit());
                }
            }
        }
        return units;
    }

    public void updateMap(ArrayList<Delta> deltaList) {
        ArrayList<Unit>[][] oldUnits = initUpdate();
        if (deltaList == null)
            return;

        for (Delta temp : deltaList) {
            Cell cellSr = null;
            if (temp.getPoint() != null)
                cellSr = this.cells[temp.getPoint().getX()][temp.getPoint().getY()];
            switch (temp.getType()) {
                case WALL_MAKE:
                    cellSr.getEdge(temp.getDirection()).setType(EdgeType.WALL);
                    break;
                case WALL_DESTROY:
                    cellSr.getEdge(temp.getDirection()).setType(EdgeType.OPEN);
                    break;
                case CELL_MOVE:
                    try {
                        Cell cellDes = getNeighborCell(cellSr, temp.getDirection());
                        Unit unit = oldUnits[cellSr.getX()][cellSr.getY()].get(0);
                        if (oldUnits[cellSr.getX()][cellSr.getY()].size() > 0)
                            oldUnits[cellSr.getX()][cellSr.getY()].remove(0);
                        if (oldUnits[cellDes.getX()][cellDes.getY()] == null)
                            oldUnits[cellDes.getX()][cellDes.getY()] = new ArrayList<Unit>(2);
                        oldUnits[cellDes.getX()][cellDes.getY()].add(unit);
                    } catch (CellIsNullException e) {
                        e.printStackTrace();
                    }
                    break;
                case MINE_DISAPPEAR:
                    MineCell mineCell = (MineCell) cellSr;
                    mineCell.setAmount(0);
                    cellSr.setType(CellType.TERRAIN);
                    mines.remove(mineCell);
                    break;
                case MINE_CHANGE:
                    MineCell mineCell2 = (MineCell) cellSr;
                    mineCell2.setAmount(mineCell2.getAmount() + temp.getChangeValue());
                    break;
                case AGENT_ARRIVE:
                    cellSr.getUnit().setArrived(true);
                    Unit unitCell2 = cellSr.getUnit();
                    unitCell2.setCell(null);
                    cellSr.setUnit(null);
                    break;
                case SPAWN:
                    Unit newUnit = new Unit();
                    newUnit.setCell(cellSr);
                    cellSr.setUnit(newUnit);
                    newUnit.setId(temp.getUnitID());
                    newUnit.setTeamId(temp.getTeamID());
                    break;
                case AGENT_KILL:
                    Unit unitCell3 = cellSr.getUnit();
                    unitCell3.setAlive(false);
                    unitCell3.setCell(null);
                    cellSr.setUnit(null);
                    break;
            }
        }

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if (oldUnits[i][j] != null && oldUnits[i][j].size() > 0) {
                    Unit unit = oldUnits[i][j].get(0);

                    // if unit disappears
                    if (unit.getCell() == null)
                        continue;

                    if (oldUnits[unit.getCell().getX()][unit.getCell().getY()] == null ||
                            oldUnits[unit.getCell().getX()][unit.getCell().getY()].size() == 0) {
                        unit.getCell().setUnit(null);
                    }
                    unit.setCell(cells[i][j]);
                    cells[i][j].setUnit(unit);
                }
            }
        }
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void print() {
        for (int i = 0; i < this.sizeY; i++) {
            for (int j = 0; j < this.sizeX; j++) {
                System.out.print(cells[j][i].getType() + "\t\t");
            }
            System.out.println();
        }
    }

    public Direction getDirectionFromCellPoint(Cell sr, Point des) throws CellIsNullException {
        for (Direction dir : Direction.values())
            if (getNeighborCell(sr, dir).equals(this.cells[des.getX()][des.getY()]))
                return dir;
        throw new CellIsNullException();
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public boolean isCellInMap(Point position) {
        return isCellInMap(position.getX(), position.getY());
    }

    public Direction getDirectionFromTwoPoints(Point p1, Point p2) throws CellIsNullException {
        Cell cell = getCellAtPoint(p1);
        return getDirectionFromCellPoint(cell, p2);
    }
}
