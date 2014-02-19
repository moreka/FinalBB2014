package javachallenge.client.teamcli;

import javachallenge.client.Client;
import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.util.*;

import java.util.Random;


/**
 * Created by mohammad on 2/5/14.
 */
public class TeamClient extends Client {

    @Override
    public void step() {
        // your code here ...
        // this is an example implementation:
        Random rnd = new Random();
        /**
         * Move section
         */
        for (Unit myUnit : myUnits) {
            move(myUnit, Direction.values()[rnd.nextInt(6)]);
            attack(myUnit.getCell(),Direction.values()[rnd.nextInt(6)]);
        }
        /**
         * Making walls section
         */
        try {
            makeWall(map.getCellAt(rnd.nextInt(map.getSizeX()), rnd.nextInt(map.getSizeY())),
                Direction.values()[rnd.nextInt(6)]);
            destroyWall(map.getCellAt(rnd.nextInt(map.getSizeX()), rnd.nextInt(map.getSizeY())),
                    Direction.values()[rnd.nextInt(6)]);
        } catch (CellIsNullException e) {
            e.printStackTrace();
        }
    }
}
