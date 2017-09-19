import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatClient implements ConnectionHandler {

    public static void main(String[] args) {

        new ChatClient();
    }

    private Socket server;
    private Connection connection;
    private BufferedReader input;
    private ChatGUI gui;

    public ChatClient() {

        this.input = new BufferedReader(new InputStreamReader(System.in));
        this.gui = new ChatGUI();
        getConnection();
        startListen();
    }

    private void startListen() {

        new Thread(this::listen).start();
    }

    private void listen() {

        try {
            while (true) {
                handleInput(gui.getMessage());
                //handleInput(input.readLine());
            }
        }

        /*catch(IOException e) {
            System.out.println("I/O exception occurred, closing");
            System.exit(1);
        }*/

        catch (InterruptedException e) {
            System.out.println("Input interrupted, closing");
            System.exit(1);
        }
    }

    private void handleInput(String input) {

        switch (input) {
            case "exit":
                // TODO: 22/04/2017 exit method
                System.out.println("Exiting");
                System.exit(0);
                break;

            case "reconnect":
                getConnection();
                break;

            // TODO: 22/04/2017 other methods

            default:
                connection.send(input);
        }
    }

    private void getConnection() {

        String hostName;
        String portNumber;

        while (true) {
            try {
                for (System.out.println("Hostname: ");
                     (hostName = input.readLine()).isEmpty();
                     System.out.println("Try again\nInput a valid host name: "))
                    ;

                for (System.out.println("Port number: ");
                     !(portNumber = input.readLine()).matches("\\d+");
                     System.out.println("Try again\nInput a valid port number: "))
                    ;

                getConnection(hostName, Integer.parseInt(portNumber));
                break;
            }

            catch (IOException e) {
                System.out.println("Those settings were incorrect, try again");
            }
        }

        System.out.println("Connected!");
    }

    private void getConnection(String hostName, int portNumber) throws IOException {

        server = new Socket(hostName, portNumber);
        connection = new Connection(this, server);
    }

    @Override
    public void notifyClose(Connection connection) {

        System.out.println("Connection closed!");
        try {
            while (true) {
                System.out.println("Do you wish to (reconnect), or (exit)?: ");
                switch (input.readLine()) {
                    case "reconnect":
                        getConnection();

                }
            }
        }

        catch (IOException e) {
            System.out.println("I/O exception occurred, closing");
            System.exit(1);
        }
    }

    @Override
    public void notifyConnection(Connection connection) {

        connection.startListen();
    }

    @Override
    public void notifyMessage(String message) {

        gui.display(message);
        System.out.println(message);
    }
}
