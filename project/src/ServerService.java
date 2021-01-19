import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerService extends Remote {

    boolean register (String username, String password) throws RemoteException;

    void registerForCallbacks(NotifyClientInterface clientInterface) throws RemoteException;

    void unregisterForCallbacks(NotifyClientInterface clientInterface) throws RemoteException;

}
