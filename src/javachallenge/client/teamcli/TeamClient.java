package javachallenge.client.teamcli;

import java.util.LinkedList;

import javachallenge.client.Client;
import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.util.Cell;
import javachallenge.util.Direction;
import javachallenge.util.Edge;
import javachallenge.util.EdgeType;
import javachallenge.util.Point;

/**
 * Created by mohammad on 2/5/14.
 */
public class TeamClient extends Client {
	// HashSet<Cell> destinations;
	// int crushedUnitNum = -1;
	int workerUnitNum = -1;

	// int layerBreakerX, layerBreakerY;
	// Direction unitDir = Direction.SOUTHWEST;
	// boolean endLayering = false;
	// int steps = 0;
	// int stepsNeeded = 0;

	@Override
	public void step() {
		// attack part
		for (int i = 0; i < getMyUnits().size(); i++) {
			Unit myUnit = getMyUnits().get(i);
			if (myUnit.isAlive()) {
				for (int j = 0; j < 6; j++) {
					Direction dir = Direction.values()[j];
					try {
						Unit tmp = map.getNeighborCell(myUnit.getCell(), dir)
								.getUnit();
						if (tmp != null && tmp.getTeamId() == 1 - getTeamID()) {
							attack(myUnit, dir);
							break;
						}
					} catch (CellIsNullException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		for (int i = 0; i < getMyUnits().size(); i++) {
			Unit myUnit = getMyUnits().get(i);
			if (myUnit.isAlive()) {
				if (myUnit.isArrived() || workerUnitNum == i)
					continue;
				Direction dir = null;
				try {
					dir = pathfinder(myUnit, map.getSpawnCell(1 - getTeamID()));
					if (dir != null) {
						Unit tmp = map.getNeighborCell(myUnit.getCell(), dir)
								.getUnit();
						if (tmp != null && tmp.getTeamId() == 1 - getTeamID())
							attack(myUnit, dir);
					}
				} catch (CellIsNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dir != null) {
					move(myUnit, dir);
				} else if (workerUnitNum == -1)
					workerUnitNum = i;
			}
		}

	}

	public Direction rotate(Direction dir) {
		Direction newDir;
		if (dir == Direction.EAST)
			newDir = Direction.SOUTHEAST;
		else if (dir == Direction.SOUTHEAST)
			newDir = Direction.SOUTHWEST;
		else if (dir == Direction.SOUTHWEST)
			newDir = Direction.WEST;
		else if (dir == Direction.WEST)
			newDir = Direction.NORTHWEST;
		else if (dir == Direction.NORTHWEST)
			newDir = Direction.NORTHEAST;
		else
			newDir = Direction.EAST;
		return newDir;
	}

	private boolean isEnemySpawnCellNear(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();
		for (int i = 0; i < 6; i++) {
			Cell neighborCell = null;
			try {
				neighborCell = map.getNeighborCell(map.getCellAt(x, y),
						Direction.values()[i]);
			} catch (CellIsNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (neighborCell.equals(map.getSpawnCell(1 - getTeamID())))
					return true;
			} catch (CellIsNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean isEnemyNear(Unit myUnit) {
		int x = myUnit.getCell().getX();
		int y = myUnit.getCell().getY();
		for (int i = 0; i < 6; i++) {
			Unit unit = null;
			try {
				unit = map.getNeighborCell(map.getCellAt(x, y),
						Direction.values()[i]).getUnit();
			} catch (CellIsNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (unit != null && unit.getTeamId() == 1 - getTeamID())
				return true;
		}
		return false;
	}

	private boolean isEnemyNear(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();
		for (int i = 0; i < 6; i++) {
			Unit unit = null;
			try {
				unit = map.getNeighborCell(map.getCellAt(x, y),
						Direction.values()[i]).getUnit();
			} catch (CellIsNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (unit != null && unit.getTeamId() == 1 - getTeamID())
				return true;
		}
		return false;
	}

	private Direction pathfinder(Unit myUnit, Cell dis) {

		String dir = null;
		try {
			dir = findPathPrivate(isAvailableMap(), myUnit.getCell().getX(),
					myUnit.getCell().getY(), dis.getX(), dis.getY());
		} catch (CellIsNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Direction direction = null;
		if (dir.equals("l"))
			direction = Direction.WEST;
		else if (dir.equals("r"))
			direction = Direction.EAST;
		else if (dir.equals("ur"))
			direction = Direction.NORTHEAST;
		else if (dir.equals("dr"))
			direction = Direction.SOUTHEAST;
		else if (dir.equals("dl"))
			direction = Direction.SOUTHWEST;
		else if (dir.equals("ul"))
			direction = Direction.NORTHWEST;
		return direction;
	}

	private boolean[][] isAvailableMap() throws CellIsNullException {
		boolean[][] isAvailableMap = new boolean[map.getSizeX()][map.getSizeY()];
		for (int x = 0; x < map.getSizeX(); x += 1) {
			for (int y = 0; y < map.getSizeY(); y += 1) {
				if (map.isCellInMap(x, y))
					isAvailableMap[x][y] = map.getCellAt(x, y).isGround();
			}
		}
		return isAvailableMap;
	}

	private String findPathPrivate(boolean[][] isAvailableMap, int sRow,
			int sCol, int dRow, int dCol) throws CellIsNullException {

		class MyPoint {
			public int row;
			public int col;

			public MyPoint(int row, int col) {
				this.row = row;
				this.col = col;
			}
		}

		int rowCount = isAvailableMap.length;
		int colCount = isAvailableMap[0].length;

		String[][] directions = new String[rowCount][colCount];
		MyPoint[][] parents = new MyPoint[rowCount][colCount];
		boolean[][] visited = new boolean[rowCount][colCount];
		boolean found = false;

		LinkedList<MyPoint> queue = new LinkedList<MyPoint>();
		queue.add(new MyPoint(sRow, sCol));

		while (!found && queue.size() > 0) {
			MyPoint point = queue.poll();
			if (point.row == dRow && point.col == dCol)
				found = true;

			String[] dirList = { "ur", "r", "dr", "dl", "l", "ul" };
			int[] deltaCol = { -1, 0, 1, 1, 0, -1 };
			int[] deltaRow = { point.col % 2, 1, point.col % 2,
					point.col % 2 - 1, -1, point.col % 2 - 1 };

			for (int i = 0; i < 6; i++) {
				int cRow = point.row + deltaRow[i];
				int cCol = point.col + deltaCol[i];
				if (cRow < 0 || cRow >= rowCount || cCol < 0
						|| cCol >= colCount)
					continue;
				boolean isWall = true;
				Edge edge = map.getCellAt(point.row, point.col).getEdge(
						map.getDirectionFromTwoPoints(new Point(point.row,
								point.col), new Point(cRow, cCol)));
				isWall = edge.getType().equals(EdgeType.WALL);
				if (visited[cRow][cCol] || !isAvailableMap[cRow][cCol]
						|| isWall)
					continue;
				queue.add(new MyPoint(cRow, cCol));
				directions[cRow][cCol] = dirList[i];
				parents[cRow][cCol] = point;
				visited[cRow][cCol] = true;
				if (cRow == dRow && cCol == dCol)
					found = true;
			}
		}

		if (!found)
			return "none";

		MyPoint point = new MyPoint(dRow, dCol);
		MyPoint parent = parents[dRow][dCol];
		while (parent != null && !(parent.row == sRow && parent.col == sCol)) {
			point = parent;
			parent = parents[point.row][point.col];
		}

		String dir = directions[point.row][point.col];
		if (dir == null)
			return "none";
		else
			return dir;
	}
}
