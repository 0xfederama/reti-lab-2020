import java.rmi.server.RemoteServer;

public class ProgCongresso extends RemoteServer implements CongressoService {

    private final Giorno[] giorni;

    public ProgCongresso() {
        giorni = new Giorno[3];
        for (int i=0; i<3; ++i) {
            giorni[i] = new Giorno();
        }
    }

    public boolean addSpeaker (int giorno, int sessione, int intervento, String nome) {
        if (giorno<1 || giorno>3) return false;
        return giorni[giorno-1].addSpeaker(sessione, intervento, nome);
    }

    public Giorno[] getProgCompleto() {
        return giorni;
    }

}
