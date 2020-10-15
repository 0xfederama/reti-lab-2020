public class Utente extends Thread {

    private final Computers pc;
    private final String utente;
    private final int id;

    public Utente (String utente, Computers pc, int id) {
        this.utente = utente;
        this.pc = pc;
        this.id = id;
    }

    @Override
    public void run() {
        int k = (int)(Math.random()*2)+1; //Ogni utente puo' entrare da 1 a 3 volte
        switch (utente) {
            case "Professore": {
                for (int i=0; i<k; ++i) {
                    System.out.println(utente + " [" + id +"] vuole entrare per la " + i + "-esima volta");
                    try {
                        pc.enterLab(utente, -1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(utente + " [" + id +"] entra per la " + i + "-esima volta");
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pc.exitLab(utente, -1);
                    System.out.println(utente + " [" + id +"] esce per la " + i + "-esima volta");
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case "Tesista": {
                for (int i=0; i<k; ++i) {
                    System.out.println(utente + " [" + id +"] vuole entrare per la " + i + "-esima volta");
                    int pcTes = (int)(Math.random()*19);
                    try {
                        pc.enterLab(utente, pcTes);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(utente + " [" + id +"] entra per la " + i + "-esima volta");
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pc.exitLab(utente, pcTes);
                    System.out.println(utente + " [" + id +"] esce per la " + i + "-esima volta");
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case "Studente": {
                for (int i=0; i<k; ++i) {
                    System.out.println(utente + " [" + id +"] vuole entrare per la " + i + "-esima volta");
                    int nPC=-1;
                    try {
                        nPC = pc.enterLab(utente, -1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(utente + " [" + id +"] entra per la " + i + "-esima volta");
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pc.exitLab(utente, nPC);
                    System.out.println(utente + " [" + id +"] esce per la " + i + "-esima volta");
                    try {
                        Thread.sleep((long) (Math.random()*100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            default:
                System.out.println("Utente non autorizzato");
        }
        System.out.println("\nUTENTE \""+utente+"\" ["+id+"] ESCE DEFINITIVAMENTE\n");
    }

}
