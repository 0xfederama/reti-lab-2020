import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class CongressoServer {
    public static void main(String[] args) {

        //Controllo degli argomenti
        if (args.length != 1) {
            System.out.println("Usage: java CongressoServer <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("ERR -port "+args[0]+": not a number");
            System.out.println("Usage: java CongressoServer <port>");
            return;
        }

        //Apro il servizio
        try {
            ProgCongresso congresso = new ProgCongresso();
            CongressoService stub = (CongressoService) UnicastRemoteObject.exportObject(congresso, 0);
            LocateRegistry.createRegistry(port);
            Registry r = LocateRegistry.getRegistry(port);
            r.rebind("CONGRESSO-SERVER", stub);
            System.out.println("Server aperto sulla porta "+port+"\n");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
}
