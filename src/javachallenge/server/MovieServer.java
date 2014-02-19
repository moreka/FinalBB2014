package javachallenge.server;

import javachallenge.message.Action;
import javachallenge.message.InitialMessage;
import javachallenge.message.ServerMessage;
import javachallenge.util.Logger;
import javachallenge.util.Map;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by merhdad on 2/20/14.
 */
public class MovieServer {
    public static int CYCLE_LENGTH = 500;

    public void run() throws InterruptedException, IOException, ClassNotFoundException {
        int num_clients = 2;

        Map map = Map.loadMap("JC.map");
        Game game = new Game(map);

        DummyGraphics graphics = new DummyGraphics(map);
        graphics.setVisible(true);

        for (int i = 0; i < num_clients; i++) {
            System.out.println("Waiting for player " + i + " to connect ...");
            System.out.println("Player " + i + " connected!");
            game.addTeam(new Team(i, Game.INITIAL_RESOURCE));
        }

//        FJframe graphics = new FJframe(game, game.getMap().getSizeY(), game.getMap().getSizeX());
//        FJpanel panel = graphics.getPanel();

        int turn = 0;

        while (!game.isEnded()) {
            System.out.println("Turn: " + (++turn));

            ServerMessage serverMessage = new ServerMessage(
                    game.getAttackDeltas(),
                    game.getWallDeltasList(),
                    game.getMoveDeltasList(),
                    game.getOtherDeltasList()
            );

            serverMessage.setGameEnded(game.isEnded());

//            for (ClientConnection c : clientConnections) {
//                c.getOut().writeObject(serverMessage);
//                c.getOut().flush();
//            }
//
//            for (ClientConnection c : clientConnections) {
//                c.setClientMessage(null);
//            }

            Thread.sleep(CYCLE_LENGTH);

            ArrayList<Action> actions = new ArrayList<Action>();

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
//            Logger.getInstance().logs(game.getAttackDeltas(), turn);
//            Logger.getInstance().logs(game.getMoveDeltasList(), turn);
//            Logger.getInstance().logs(game.getWallDeltasList(), turn);
//            Logger.getInstance().logs(game.getOtherDeltasList(), turn);
        }

//        for (ClientConnection c : clientConnections) {
//            c.getOut().writeObject(
//                    new ServerMessage(true)
//            );
//            c.getOut().flush();
//        }

        Logger.getInstance().close();
    }

    public static void main(String[] args) {
        try {
            new Server().run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
