public class Main {
    public static void main(String[] args) throws InterruptedException {

        Dropbox db = new Dropbox();

        new Thread(new Consumer(db, true)).start();
        new Thread(new Consumer(db, true)).start();
        new Thread(new Consumer(db, false)).start();
        new Thread(new Producer(db)).start();

        System.out.println("Tutti i thread sono partiti");

    }
}
