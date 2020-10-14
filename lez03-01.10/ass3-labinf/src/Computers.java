import java.util.Vector;
import java.util.concurrent.locks.*;

public class Computers {

    private final int pcTesisti;
    private final int[] computers; //0 libero, 1 occupato
    private final Lock lockPC = new ReentrantLock();
    private final Condition condProf = lockPC.newCondition();
    private final Condition condTes = lockPC.newCondition();
    private final Condition condStud = lockPC.newCondition();
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

    public int enterLab(String utente) throws InterruptedException {
        int nPC = -1;
        switch (utente) {
            case "Professore": {
                lockPC.lock();
                while (pcEmpty()<20) {
                    utentiAttesa.add(utente);
                    condProf.await();
                    utentiAttesa.remove(utente);
                }
                for (int i=0; i<20; ++i) {
                    computers[i] = 1;
                }
                lockPC.unlock();
                break;
            }
            case "Tesista": {
                lockPC.lock();
                while (computers[pcTesisti]==1 || utentiAttesa.contains("Professore")) {
                    utentiAttesa.add(utente);
                    condTes.await();
                    utentiAttesa.remove(utente);
                }
                computers[pcTesisti] = 1;
                lockPC.unlock();
                break;
            }
            case "Studente": {
                lockPC.lock();
                while (pcEmpty()==0 || utentiAttesa.contains("Professore") || (pcEmpty()==1 && computers[pcTesisti]==0 && utentiAttesa.contains("Tesista"))) {
                    utentiAttesa.add(utente);
                    condStud.await();
                    utentiAttesa.remove(utente);
                }
                for (int i=0; i<20; ++i) {
                    if (computers[i]==0) {
                        computers[i]=1;
                        nPC=i;
                        break;
                    }
                }
                lockPC.unlock();
                break;
            }
            default:
                System.out.println("Utente non autorizzato");
        }
        return nPC;
    }

    public void exitLab(String utente, int nPC) {
        switch (utente) {
            case "Professore": {
                lockPC.lock();
                for (int i = 0; i < 20; ++i) {
                    computers[i] = 0;
                }
                if (utentiAttesa.contains("Professore")) condProf.signalAll();
                else if (utentiAttesa.contains("Tesista")) condTes.signalAll();
                else if (utentiAttesa.contains("Studente")) condStud.signalAll();
                lockPC.unlock();
                break;
            }
            case "Tesista": {
                lockPC.lock();
                computers[pcTesisti]=0;
                if (utentiAttesa.contains("Professore")) condProf.signalAll();
                    else if (utentiAttesa.contains("Tesista")) condTes.signalAll();
                    else if (utentiAttesa.contains("Studente")) condStud.signalAll();
                lockPC.unlock();
                break;
            }
            case "Studente": {
                lockPC.lock();
                computers[nPC]=0;
                if (utentiAttesa.contains("Professore")) condProf.signalAll();
                    else if (utentiAttesa.contains("Tesista")) condTes.signalAll();
                    else if (utentiAttesa.contains("Studente")) condStud.signalAll();
                lockPC.unlock();
                break;
            }
            default:
                System.out.println("Utente non autorizzato");
        }

    }

}
