import java.io.*;
import java.net.Socket;

/**
 * Representation of a connection to a socket.
 */
class Connection {
    private ConnectionHandler handler;
    private BufferedReader input;
    private PrintWriter output;

    /**
     * Creates a new connection to a client.
     * @param socket The socket to take a client from.
     * @throws IOException If the server or client sockets are invalid.
     */
    public Connection(ConnectionHandler handler, Socket socket) throws IOException {
        this.handler = handler;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        handler.notifyConnection(this);
    }

    /**
     * Sends a message to the client
     * @param message The message to send.
     */
    public void send(String message) {
        output.println(message);
    }

    /**
     * Begins the message listening loop.
     */
    public void startListen() {
        new Thread(this::listen).start();
    }

    /**
     * Listens to messages until the connection throws an exception.
     */
    private void listen() {
        try {
            while (true) {
                handler.notifyMessage(input.readLine());
            }
        }

        catch (IOException e) {
            handler.notifyClose(this);
        }
    }
}