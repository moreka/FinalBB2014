package javachallenge.client.teamcli;

import javachallenge.client.Client;
import javachallenge.exceptions.CellIsNullException;
import javachallenge.units.Unit;
import javachallenge.util.Cell;
import javachallenge.util.Direction;
import javachallenge.util.Edge;
import javachallenge.util.EdgeType;

public class TeamClient extends Client {

    @Override
    public void step() {
        if (getTurn() > 2) {
            if (getTurn() % 2 == 0) {
                move(getMyUnits().get(0), Direction.SOUTHWEST);
                move(getMyUnits().get(0), Direction.SOUTHWEST);
            } else {
                move(getMyUnits().get(0), Direction.SOUTHEAST);
                move(getMyUnits().get(0), Direction.EAST);
            }
            attack(getMyUnits().get(0), Direction.SOUTHEAST);
            attack(getMyUnits().get(0), Direction.WEST);
            attack(getMyUnits().get(0), Direction.NORTHEAST);
        }
    }
}
