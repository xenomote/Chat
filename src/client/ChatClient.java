package client;

import connections.*;
import graphics.ChatGUI;

import java.io.IOException;
import java.net.Socket;

public class ChatClient implements ConnectionHandler {

    public static void main(String[] args) {

        new ChatClient();
    }

    private Connection connection;
    private ChatGUI gui;
    private String username;

    public ChatClient() {

        this.gui = new ChatGUI();
        this.username = "anonymous";

        startListen();
    }

    private void startListen() {

        new Thread(this::listen).start();
    }

    private void listen() {

        while (true) handleInput(gui.getMessage());
    }

    /**
     * Deals with user input, responding to keywords and performing the relevant actions.
     *
     * @param input the message or command to be handled
     */
    private void handleInput(String input) {

        switch (input) {
            case "-exit":
                // TODO: 22/04/2017 exit method
                gui.display("Exiting");
                System.exit(0);
                break;

            case "-connect":
                connect();
                break;

            case "-username":
                setUserName();
                break;

            case "-help":
                displayHelp();
                break;

            // TODO: 22/04/2017 other methods

            default:
                if (connection != null && connection.isOpen()) {
                    connection.send(username + " : " + input);
                }

                else {
                    gui.display("Unrecognised command, try -help");
                }
        }
    }

    private void setUserName() {

        //Get a new user name
        do gui.display("new username: ");
        while ((username = gui.getMessage()).isEmpty());
    }

    private void displayHelp() {

        gui.display("<br>" +
                "Commands are:" + "<br>" +
                "-help : displays this help menu" + "<br>" +
                "-connect : initiates connection to a chat server" + "<br>" +
                "-exit : quits the program" + "<br>");
    }

    /**
     * Asks the user for connection details and attempts connection until a successful connection is made
     */
    private void connect() {

        while (true) {
            try {
                //Get the connection
                connect(getConnectionInfo());
                break;
            }

            catch (IOException e) {
                gui.display("Those settings were incorrect, try again");
            }
        }

        gui.display("Connected!");
    }

    /**
     * Requests details of the chat server to connect to from the user.
     *
     * @return The connectionInfo object representing the user's input
     * @throws IOException If there is an unexpected IO error during input
     */
    private ConnectionInfo getConnectionInfo() {

        String hostName;
        String portNumber;

        //Get the host name
        do gui.display("Hostname: ");
        while ((hostName = gui.getMessage()).isEmpty());

        //Get the port number
        do gui.display("Port number: ");
        while (!(portNumber = gui.getMessage()).matches("\\d+"));

        return new ConnectionInfo(hostName, Integer.parseInt(portNumber));
    }

    private void connect(ConnectionInfo info) throws IOException {

        Socket server = new Socket(info.getHostname(), info.getPortNumber());
        connection = new Connection(this, server);
    }

    @Override
    public void notifyClose(Connection connection) {

        gui.display("Connection closed!");
    }

    @Override
    public void notifyConnection(Connection connection) {

        connection.startListen();
    }

    @Override
    public void notifyMessage(String message) {

        gui.display(message);
    }
}
