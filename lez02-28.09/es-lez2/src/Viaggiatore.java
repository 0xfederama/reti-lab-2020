import java.util.concurrent.*;

public class Viaggiatore implements Runnable {

    private int id;
    public Viaggiatore(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("Viaggiatore "+id+" sta acquistando un biglietto");
        try {
            Thread.sleep((long)(Math.random()*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Viaggiatore "+id+" ha acquistato un biglietto");
    }
}

class MainViaggiatore {
    public static void main(String[] args) {

        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

        for (int i=0; i<50; i++) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (pool.getQueue().size() >= 10) {
                System.out.println("Traveler "+i+", sala esaurita");
            } else {
                pool.execute(new Viaggiatore(i));
            }
        }

        pool.shutdown();
        try {
            pool.awaitTermination(5000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
