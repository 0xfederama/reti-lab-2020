import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainServer {
    public static void main(String[] args) {

        //Setup RMI server
        int rmiPort = 11111;
        WorthServer server = new WorthServer();
        try {
            ServerService stub;
            Registry registry;
            stub = (ServerService) UnicastRemoteObject.exportObject(server, 0);
            LocateRegistry.createRegistry(rmiPort);
            registry = LocateRegistry.getRegistry(rmiPort);
            registry.rebind("ServerRMI", stub);
        } catch (RemoteException e) {
            System.out.println("Remote exception setting up RMI... quitting");
            e.printStackTrace();
            return;
        }

        //Launching server
        server.start();

        System.out.println("Server closed");

    }
}
