public class Producer extends Thread {

    private final Dropbox db;

    public Producer(Dropbox db) {
        this.db = db;
    }

    public void run() {
        while (true) {
            int rand = (int)(Math.random()*100);
            db.put(rand);
            System.out.println("Producer ha inserito "+rand+" in Dropbox");
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
