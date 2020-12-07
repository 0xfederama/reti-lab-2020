import java.io.Serializable;

public class Giorno implements Serializable {

    private final String[][] programma;

    public Giorno() {
        programma = new String[12][5];
        for (int i=0; i<12; i++) {
            for (int j=0; j<5; ++j) {
                programma[i][j]="-";
            }
        }
    }

    public boolean addSpeaker(int sessione, int intervento, String nome) {
        if (sessione<1 || sessione>12 || intervento<1 || intervento>5) return false;
        if (programma[sessione-1][intervento-1].equals("-")) {
            programma[sessione-1][intervento-1] = nome;
            System.out.println("Ho aggiunto "+nome+" alla sessione "+sessione+" e interv "+intervento);
            return true;
        } else {
            return false;
        }
    }

    public String[][] getProgramma() {
        return programma;
    }

}
