import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
    private static final String host = "localhost";
    private static final int port = 9090;

    public static void main(String[] args) {
        View v = new View(host, port);
        v.setVisible(true);
    }
}
