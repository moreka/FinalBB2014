package javachallenge.util;

/**
 * Created by mohammad on 2/5/14.
 */
public enum Direction {
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTHWEST,
    WEST,
    NORTHWEST;

    public Direction[] getDirections() {
        Direction[] dir = new Direction[6];
        dir[0] = Direction.NORTHEAST;
        dir[1] = Direction.EAST;
        dir[2] = Direction.SOUTHEAST;
        dir[3] = Direction.SOUTHWEST;
        dir[4] = Direction.WEST;
        dir[5] = Direction.NORTHWEST;
        return dir;
    }
}