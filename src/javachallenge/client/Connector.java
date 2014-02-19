package javachallenge.client;

import javachallenge.client.teamcli.TeamClient;
import javachallenge.message.ClientMessage;
import javachallenge.message.InitialMessage;
import javachallenge.message.ServerMessage;
import javachallenge.util.Cell;
import javachallenge.util.CellType;
import javachallenge.util.Map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by mohammad on 2/5/14.
 */
public class Connector {

    private static final int WAIT_TIME = 50;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Object lock = new Object();
    ServerMessage serverMessage = null;
    ServerMessage otherThreadMessage = null;
    Client client;
    boolean isGameEnded = false;

    public Connector(String server, int port) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(server, port);
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());

        InitialMessage initialMessage = (InitialMessage) in.readObject();
        client = new TeamClient();
        client.setTeamID(initialMessage.getTeamId());
        client.setResources(initialMessage.getResource());
        client.map = Map.loadMapFromString(initialMessage.getMap());

        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isGameEnded) {
                        ServerMessage tmp = (ServerMessage) in.readObject();
//                        System.out.println("data recieved from server");
                        synchronized (lock) {
                            serverMessage = tmp;
                        }
                        isGameEnded = serverMessage.isGameEnded();
                    }
                } catch (IOException e) {
                    System.err.println("Cannot establish connection to server");
                    isGameEnded = true;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    while (!isGameEnded) {
                        synchronized (lock) {
                            otherThreadMessage = serverMessage;
                        }
                        if (otherThreadMessage != null) {
                            synchronized (lock) {
                                serverMessage = null;
                            }
                            client.init();
                            client.update(otherThreadMessage);
                            client.step();
                            ClientMessage message = client.end();
//                            System.out.println("Writing object to server ...");
                            out.writeObject(message);
                        }
                        else {
                            Thread.sleep(WAIT_TIME);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Cannot establish connection to server");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        try {
            new Connector("127.0.0.1", 20140);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
