/**
 * Created by mohammad on 2/4/14.
 */

package javachallenge.server;

import javachallenge.message.*;
import javachallenge.util.Logger;
import javachallenge.util.Map;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    public static int CYCLE_LENGTH = 500;
    public static int PORT = 20140;

    public void run() throws InterruptedException, IOException, ClassNotFoundException {
        int num_clients = 2;

        String team1 = "Koskeshaa", team2 = "Koondehaa";

        ClientConnection[] clientConnections = new ClientConnection[num_clients];

        ServerSocket serverSocket = new ServerSocket(PORT);

        Map map = Map.loadMap("JC.map");
        Game game = new Game(map);

        DummyGraphics graphics = new DummyGraphics(map);
        graphics.setVisible(true);

        for (int i = 0; i < num_clients; i++) {
            System.out.println("Waiting for player " + i + " to connect ...");
            clientConnections[i] = new ClientConnection(serverSocket.accept());
            System.out.println("Player " + i + " connected!");
        }

        graphics.setTeam1NameLabel(team1);
        graphics.setTeam2NameLabel(team2);

        int i = 0;
        for (ClientConnection c : clientConnections) {
            game.addTeam(new Team(i, Game.INITIAL_RESOURCE));
            c.getOut().writeObject(new InitialMessage(map.getString(), i, Game.INITIAL_RESOURCE));
            c.getOut().flush();
            i++;
        }

        int turn = 0;

        while (!game.isEnded()) {
            System.out.println("Turn: " + (++turn));

            graphics.setTurnNumberLabel(turn);

            ServerMessage serverMessage = new ServerMessage(
                    game.getAttackDeltas(),
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
            Logger.getInstance().logs(actions, turn);
            game.handleActions(actions);
            game.endTurn();
            game.getMap().updateMap(game.getOtherDeltasList());
            graphics.startAnimation();
            graphics.setTeam1ScoreLabel(game.getTeam(0).getScore());
            graphics.setTeam2ScoreLabel(game.getTeam(1).getScore());
            graphics.setTeam1ResourceLabel(game.getTeam(0).getResource());
            graphics.setTeam2ResourceLabel(game.getTeam(1).getResource());
        }

        for (ClientConnection c : clientConnections) {
            c.getOut().writeObject(
                    new ServerMessage(true)
            );
            c.getOut().flush();
        }

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
