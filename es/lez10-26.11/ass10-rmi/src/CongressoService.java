import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CongressoService extends Remote {

    boolean addSpeaker(int giorno, int sessione, int intervento, String nome) throws RemoteException;
    Giorno[] getProgCompleto() throws RemoteException;

}
