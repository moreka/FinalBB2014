package javachallenge.message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mohammad on 2/5/14.
 */
public class ServerMessage
        implements Serializable {

    private static final long serialVersionUID = 1855437094950563362L;
    private ArrayList<Delta> attackDeltaList,
            wallDeltaList,    // goes to updateMap()
            moveDeltaList,                     // goes to updateMap()
            otherDeltaList;                     // goes to Client, including Agent_disappear, resource_changed
    private boolean isGameEnded = false;

    public ServerMessage() {
        this.attackDeltaList = new ArrayList<Delta>();
        this.wallDeltaList = new ArrayList<Delta>();
        this.moveDeltaList = new ArrayList<Delta>();
        this.otherDeltaList = new ArrayList<Delta>();
    }

    public ServerMessage(ArrayList<Delta> attackDeltaList, ArrayList<Delta> wallDeltaList, ArrayList<Delta> moveDeltaList, ArrayList<Delta> otherDeltaList) {

        this.attackDeltaList = attackDeltaList;
        this.wallDeltaList = wallDeltaList;
        this.moveDeltaList = moveDeltaList;
        this.otherDeltaList = otherDeltaList;
    }

    public ArrayList<Delta> getAttackDeltaList() {
        return attackDeltaList;
    }

    public void setAttackDeltaList(ArrayList<Delta> attackDeltaList) {
        this.attackDeltaList = attackDeltaList;
    }

    public ArrayList<Delta> getWallDeltaList() {
        return wallDeltaList;
    }

    public void setWallDeltaList(ArrayList<Delta> wallDeltaList) {
        this.wallDeltaList = wallDeltaList;
    }

    public ArrayList<Delta> getMoveDeltaList() {
        return moveDeltaList;
    }

    public void setMoveDeltaList(ArrayList<Delta> moveDeltaList) {
        this.moveDeltaList = moveDeltaList;
    }

    public ArrayList<Delta> getOtherDeltaList() {
        return otherDeltaList;
    }

    public void setOtherDeltaList(ArrayList<Delta> otherDeltaList) {
        this.otherDeltaList = otherDeltaList;
    }

    public boolean isGameEnded() {
        return isGameEnded;
    }

    public void setGameEnded(boolean isGameEnded) {
        this.isGameEnded = isGameEnded;
    }
}
