package javachallenge.client.teamcli;

import javachallenge.client.Client;
import javachallenge.units.Unit;
import javachallenge.util.*;

import java.util.Random;


/**
 * Created by mohammad on 2/5/14.
 */
public class TeamClient extends Client {
    int turn = 0;
    @Override

    public void step() {
        // your code here ...
        // this is an example implementation:
        Random rnd = new Random();
        /**
         * Move section
         */
        for (Unit myUnit : myUnits) {
            //move(myUnit, Direction.values()[rnd.nextInt(6)]);
            if (turn < 7)
                move(myUnits.get(0), Direction.SOUTHWEST);
        }
        turn++;
        /**
         * Making walls section
         */
        makeWall(map.getCellAt(rnd.nextInt(map.getSizeX()), rnd.nextInt(map.getSizeY())),
            Direction.values()[rnd.nextInt(6)]);
    }
}
