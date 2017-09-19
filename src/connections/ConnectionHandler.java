package connections;

public interface ConnectionHandler {

    void notifyConnection(Connection connection);

    void notifyMessage(String message);

    void notifyClose(Connection connection);
}
