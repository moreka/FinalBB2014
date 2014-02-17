package javachallenge.message;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mohammad on 2/5/14.
 */
public class ClientMessage
        implements Serializable {

    private static final long serialVersionUID = -8150560957430239238L;
    private ArrayList<Action> actions;

    public ClientMessage(ArrayList<Action> actions) {
        this.actions = actions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
    }
}
