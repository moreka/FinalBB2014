package javachallenge.util;

/**
 * Created by merhdad on 2/15/14.
 */
public class MapHelper {
    private int sizeX;
    private int sizeY;
    private CellType[][] cells;
    private Point spawn1;
    private Point destination1;
    private Point spawn2;
    private Point destination2;
    private Point[] mines;
    private int amountMines;

    public MapHelper(int sizeX, int sizeY, CellType[][] cells, Point spawn1, Point destination1, Point spawn2, Point destination2, Point[] mines, int amountMines) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.cells = cells;
        this.spawn1 = spawn1;
        this.destination1 = destination1;
        this.spawn2 = spawn2;
        this.destination2 = destination2;
        this.mines = mines;
        this.amountMines = amountMines;
    }

    public int getAmountMines() {
        return amountMines;
    }

    public MapHelper(Map map) {
        sizeX = map.getSizeX();
        sizeY = map.getSizeY();
        cells = new CellType[sizeX][sizeY];
        for (int i = 0; i < map.getSizeX(); i++)
            for (int j = 0; j < map.getSizeY(); j++)
                cells[i][j] = map.getCellAt(i, j).getType();
        spawn1 = map.getSpawnPoint(0);
        destination1 = map.getDestinationPoint(0);
        spawn2 = map.getSpawnPoint(1);
        destination2 = map.getDestinationPoint(1);
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public CellType[][] getCells() {
        return cells;
    }

    public Point getSpawn1() {
        return spawn1;
    }

    public Point getDestination1() {
        return destination1;
    }

    public Point getSpawn2() {
        return spawn2;
    }

    public Point getDestination2() {
        return destination2;
    }

    public Point[] getMines() {
        return mines;
    }
}
