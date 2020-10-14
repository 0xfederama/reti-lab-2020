import java.util.Vector;

public class Computers {

    private final int pcTesisti;
    private final int[] computers; //0 libero, 1 occupato
    private final Vector<String> utentiAttesa = new Vector<>();

    public Computers(int pcTesisti) {
        this.pcTesisti = pcTesisti;
        this.computers = new int[20];
        for (int i=0; i<20; ++i) {
            computers[i]=0;
        }
    }

    private int pcEmpty() {
        int size=0;
        for (int i=0; i<20; ++i) {
            if (computers[i]==0) size++;
        }
        return size;
    }

    public synchronized int enterLab(String utente) throws InterruptedException {
        int nPC = -1;
        switch (utente) {
            case "Professore": {
                while (pcEmpty()<20) {
                    utentiAttesa.add(utente);
                    wait();
                    utentiAttesa.remove(utente);
                }
                for (int i=0; i<20; ++i) {
                    computers[i] = 1;
                }
                break;
            }
            case "Tesista": {
                while (computers[pcTesisti]==1 || utentiAttesa.contains("Professore")) {
                    utentiAttesa.add(utente);
                    wait();
                    utentiAttesa.remove(utente);
                }
                computers[pcTesisti] = 1;
                break;
            }
            case "Studente": {
                while (pcEmpty()==0 || utentiAttesa.contains("Professore") || (pcEmpty()==1 && computers[pcTesisti]==0 && utentiAttesa.contains("Tesista"))) {
                    utentiAttesa.add(utente);
                    wait();
                    utentiAttesa.remove(utente);
                }
                for (int i=0; i<20; ++i) {
                    if (computers[i]==0) {
                        computers[i]=1;
                        nPC=i;
                        break;
                    }
                }
                break;
            }
            default:
                System.out.println("Utente non autorizzato");
        }
        return nPC;
    }

    public synchronized void exitLab(String utente, int nPC) {
        switch (utente) {
            case "Professore": {
                for (int i = 0; i < 20; ++i) {
                    computers[i] = 0;
                }
                notifyAll();
                break;
            }
            case "Tesista": {
                computers[pcTesisti]=0;
                notifyAll();
                break;
            }
            case "Studente": {
                computers[nPC]=0;
                notifyAll();
                break;
            }
            default:
                System.out.println("Utente non autorizzato");
        }

    }

}
