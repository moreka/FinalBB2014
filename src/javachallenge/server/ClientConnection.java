package javachallenge.server;

import javachallenge.message.ClientMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by mohammad on 2/6/14.
 */
public class ClientConnection {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientMessage clientMessage;

    public ClientConnection(Socket socket) throws IOException, ClassNotFoundException {
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        ClientMessage tmp = (ClientMessage) in.readObject();
                        System.out.println("client message recieved");
                        synchronized (ClientConnection.this) {
                            ClientConnection.this.clientMessage = tmp;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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
