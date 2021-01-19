import java.util.concurrent.locks.*;

public class Computers {

    private final int[] computers; //0 libero, 1 occupato
    private final int[] computersTesisti; //0 non richiesto, 1 richiesto
    private final Lock lockPC = new ReentrantLock();
    private final Condition condProf = lockPC.newCondition();
    private final Condition condTes = lockPC.newCondition();
    private final Condition condStud = lockPC.newCondition();
    private int nProf = 0;
    private int nTes = 0;
    private int nStud = 0;

    public Computers() {
        computers = new int[20];
        for (int i=0; i<20; ++i) {
            computers[i]=0;
        }
        computersTesisti = new int[20];
        for (int i=0; i<20; ++i) {
            computersTesisti[i]=0;
        }
    }

    private int pcEmpty() {
        int size=0;
        for (int i=0; i<20; ++i) {
            if (computers[i]==0) size++;
        }
        return size;
    }

    private boolean pcStudDisp() {
        boolean out = false;
        for (int i=0; i<20; ++i) {
            if (computers[i]==0 && computersTesisti[i]==0) {
                out=true;
                break;
            }
        }
        return out;
    }

    public int enterLab(String utente, int pcTesista) throws InterruptedException {
        int nPC = -1;
        switch (utente) {
            case "Professore": {
                lockPC.lock();
                nProf++;
                while (pcEmpty()<20) {
                    condProf.await();
                }
                nProf--;
                for (int i=0; i<20; ++i) {
                    computers[i] = 1;
                }
                lockPC.unlock();
                break;
            }
            case "Tesista": {
                lockPC.lock();
                nTes++;
                computersTesisti[pcTesista] = 1;
                while (computers[pcTesista]==1 || nProf>0) {
                    condTes.await();
                }
                nTes--;
                computers[pcTesista] = 1;
                nPC = pcTesista;
                lockPC.unlock();
                break;
            }
            case "Studente": {
                lockPC.lock();
                nStud++;
                while (pcEmpty()==0 || nProf>0 || !pcStudDisp()) {
                    condStud.await();
                }
                nStud--;
                for (int i=0; i<20; ++i) {
                    if (computers[i]==0 && computersTesisti[i]==0) {
                        computers[i] = 1;
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
                if (nProf>0) condProf.signalAll();
                else {
                    if (nTes>0) condTes.signalAll();
                    if (nStud>0) condStud.signalAll();
                }
                lockPC.unlock();
                break;
            }
            case "Tesista":
            case "Studente": {
                lockPC.lock();
                computers[nPC]=0;
                if (nProf>0) condProf.signalAll();
                else if (nTes>0 && computersTesisti[nPC]==1) condTes.signalAll();
                else if (nStud>0) condStud.signalAll();
                lockPC.unlock();
                break;
            }
            default:
                System.out.println("Utente non autorizzato");
        }

    }

}
