package javachallenge.server;

import javachallenge.mapParser.Parser;
import javachallenge.message.Action;
import javachallenge.message.InitialMessage;
import javachallenge.message.ServerMessage;
import javachallenge.util.Logger;
import javachallenge.util.Map;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by merhdad on 2/20/14.
 */
public class MovieServer {
    public static int CYCLE_LENGTH = 500;

    public void run() throws InterruptedException, IOException, ClassNotFoundException {
        int num_clients = 2;

        Map map = Map.loadMap("JC.map");
        Game game = new Game(map);

        for (int i = 0; i < num_clients; i++) {
            System.out.println("Waiting for player " + i + " to connect ...");
            System.out.println("Player " + i + " connected!");
            game.addTeam(new Team(i, Game.INITIAL_RESOURCE));
        }

        DummyGraphics graphics = new DummyGraphics(map);
        graphics.setVisible(true);

//        FJframe graphics = new FJframe(game, game.getMap().getSizeY(), game.getMap().getSizeX());
//        FJpanel panel = graphics.getPanel();

        int turn = 0;

        Parser parser = new Parser();

        HashMap<Integer, ArrayList<Action>> allActions = parser.logParser("JavaChallenge.log");

        while (!game.isEnded()) {
            System.out.println("Turn: " + (++turn));

            Thread.sleep(CYCLE_LENGTH);

            ArrayList<Action> actions = allActions.get(turn);

//            actions.addAll()
//            for (ClientConnection c : clientConnections) {
//                if (c.getClientMessage() != null)
//                    if (c.getClientMessage().getActions() != null)
//                        actions.addAll(c.getClientMessage().getActions());
//            }

            game.initTurn(turn);
            game.handleActions(actions);
            game.endTurn();
            game.getMap().updateMap(game.getOtherDeltasList());
            graphics.startAnimation();
        }

//        for (ClientConnection c : clientConnections) {
//            c.getOut().writeObject(
//                    new ServerMessage(true)
//            );
//            c.getOut().flush();
//        }
    }

    public static void main(String[] args) {
        try {
            new MovieServer().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
