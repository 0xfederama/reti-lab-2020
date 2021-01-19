public class Computers {

    private final int[] computersTesisti; //0 non richiesto, 1 richiesto
    private final int[] computers; //0 libero, 1 occupato
    private int nProf = 0;

    public Computers() {
        this.computers = new int[20];
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
        for (int i = 0; i < 20; ++i) {
            if (computers[i] == 0 && computersTesisti[i] == 0) {
                out = true;
                break;
            }
        }
        return out;
    }

    public synchronized int enterLab(String utente, int pcTesista) throws InterruptedException {
        int nPC = -1;
        switch (utente) {
            case "Professore": {
                nProf++;
                while (pcEmpty()<20) {
                    wait();
                }
                nProf--;
                for (int i=0; i<20; ++i) {
                    computers[i] = 1;
                }
                break;
            }
            case "Tesista": {
                while (computers[pcTesista]==1 || nProf>0) {
                    wait();
                }
                computers[pcTesista] = 1;
                break;
            }
            case "Studente": {
                while (pcEmpty()==0 || nProf>0 || !pcStudDisp()) {
                    wait();
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
            case "Tesista":
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
