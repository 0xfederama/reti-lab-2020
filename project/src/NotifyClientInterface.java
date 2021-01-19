import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface NotifyClientInterface extends Remote {

    void notifyUsers(HashMap<String, String> users) throws RemoteException;

    void notifySockets(HashMap<String, String> sockets) throws RemoteException;

}
