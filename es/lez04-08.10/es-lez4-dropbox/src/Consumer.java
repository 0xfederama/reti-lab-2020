public class Consumer extends Thread {

    private final Dropbox db;
    private final boolean pari;

    public Consumer(Dropbox db, boolean pari) {
        this.db = db;
        this.pari = pari;
    }

    public void run() {
        while (true) {
            int get = db.take(pari);
            System.out.println("Thread consumer ["+Thread.currentThread().getId()+"] ha preso "+get+" da Dropbox");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
