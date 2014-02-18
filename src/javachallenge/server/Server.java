/**
 * Created by mohammad on 2/4/14.
 */

package javachallenge.server;

import javachallenge.message.*;
import javachallenge.util.Map;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    public static int CYCLE_LENGTH = 500;
    public static int PORT = 20140;

    public void run() throws InterruptedException, IOException, ClassNotFoundException {
        int num_clients = 2;

        ClientConnection[] clientConnections = new ClientConnection[num_clients];

        ServerSocket serverSocket = new ServerSocket(PORT);

        for (int i = 0; i < num_clients; i++) {
            System.out.println("Waiting for player " + i + " to connect ...");
            clientConnections[i] = new ClientConnection(serverSocket.accept());
            System.out.println("Player " + i + " connected!");
        }

        Map map = Map.loadMap("Easy.map");
        Game game = new Game(map);

        int i = 0;
        for (ClientConnection c : clientConnections) {
            c.getOut().writeObject(new InitialMessage(map.getString(), i++, Game.INITIAL_RESOURCE));
            c.getOut().flush();
        }

//        FJframe graphics = new FJframe(game, game.getMap().getSizeY(), game.getMap().getSizeX());
//        FJpanel panel = graphics.getPanel();

        DummyGraphics graphics = new DummyGraphics(map);
        graphics.setVisible(true);

        int turn = 0;

        while (!game.isEnded() && turn < 700) {
            System.out.println("Turn: " + (++turn));

            ServerMessage serverMessage = new ServerMessage(
                    game.getWallDeltasList(),
                    game.getMoveDeltasList(),
                    game.getOtherDeltasList()
            );

            serverMessage.setGameEnded(game.isEnded());

            for (ClientConnection c : clientConnections) {
                c.getOut().writeObject(serverMessage);
                c.getOut().flush();
            }

            for (ClientConnection c : clientConnections) {
                c.setClientMessage(null);
            }

            Thread.sleep(CYCLE_LENGTH);

            ArrayList<Action> actions = new ArrayList<Action>();

            for (ClientConnection c : clientConnections) {
                if (c.getClientMessage() != null)
                    if (c.getClientMessage().getActions() != null)
                        actions.addAll(c.getClientMessage().getActions());
            }

            game.initTurn(turn);
            game.handleActions(actions);
            graphics.repaint();
            game.endTurn();
            game.getMap().updateMap(game.getOtherDeltasList());
        }
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
