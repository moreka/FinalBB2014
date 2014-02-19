package javachallenge.server;

import javachallenge.message.ClientMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientMessage clientMessage;

    public ClientConnection(Socket socket) throws IOException, ClassNotFoundException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        Thread clientThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        ClientMessage tmp = (ClientMessage) in.readObject();
//                        System.out.println("client message recieved");
                        synchronized (ClientConnection.this) {
                            ClientConnection.this.clientMessage = tmp;
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Client no longer connected to game");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        clientThread.setDaemon(true);
        clientThread.start();
    }

    public synchronized ClientMessage getClientMessage() {
        return clientMessage;
    }

    public synchronized void setClientMessage(ClientMessage clientMessage) {
        this.clientMessage = clientMessage;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public ObjectInputStream getIn() {
        return in;
    }
}
