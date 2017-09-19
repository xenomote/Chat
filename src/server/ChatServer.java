package server;

import connections.*;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * A server class which waits for connections and sends messages received from clients to all other clients.
 */
public class ChatServer implements ConnectionHandler {

    public static void main(String[] args) throws IOException {

        new ChatServer(10);
    }

    private ServerSocket server;
    private ArrayList<Connection> clients;
    private final int maxClients;

    /**
     * Starts a new chat server.
     *
     * @param maxClients The maximum clients the server will serve.
     * @throws IOException If the server socket could not be opened.
     */
    public ChatServer(int maxClients) throws IOException {

        this.server = new ServerSocket(0);
        this.clients = new ArrayList<>();
        this.maxClients = maxClients;
        getNewClients();
    }

    /**
     * Tries to get a new client unless there are already the maximum client number.
     */
    private void getNewClients() {

        while (clients.size() < maxClients) {
            findNewClient();
        }
    }

    /**
     * Adds a new client to the list of clients permitting a successful connection
     */
    private void findNewClient() {
        //Try to add a new client
        try {
            System.out.println("Waiting for client on port " + server.getLocalPort());
            clients.add(new Connection(this, server.accept()));
            notifyMessage("Client " + clients.size() + " connected!");
        }

        //If there is an IOException then the client simply didn't connect
        catch (IOException ignored) {/**/}

    }

    /**
     * Notifies a successful connection.
     *
     * @param connection The connection which succeeded.
     */
    @Override
    public void notifyConnection(Connection connection) {

        connection.startListen();
    }

    /**
     * Notifies a message receipt event.
     *
     * @param message The message received.
     */
    @Override
    public void notifyMessage(String message) {

        System.out.println(message);
        for (Connection client : clients) {
            client.send(message);
        }
    }

    /**
     * Notifies a connection closure event.
     *
     * @param connection The connection which closed.
     */
    @Override
    public void notifyClose(Connection connection) {

        notifyMessage("*A client left*");
        clients.remove(connection);
        getNewClients();
    }
}