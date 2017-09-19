package connections;

public class ConnectionInfo {
    private String hostname;
    private int portNumber;

    public ConnectionInfo(String hostname, int portNumber) {

        this.hostname = hostname;
        this.portNumber = portNumber;
    }

    public String getHostname() {

        return hostname;
    }

    public int getPortNumber() {

        return portNumber;
    }
}
