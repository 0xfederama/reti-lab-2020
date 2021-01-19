import java.util.concurrent.*;

public class Sportelli {

    private ThreadPoolExecutor pool;
    private ArrayBlockingQueue<Runnable> coda;
    private int dimP;

    public Sportelli(int dimP) {
        this.dimP = dimP;
        coda = new ArrayBlockingQueue<Runnable>(dimP);
        pool = new ThreadPoolExecutor(4, 4, 2, TimeUnit.SECONDS, coda);
    }

    public void executeTask(TaskCliente cliente) {
        pool.execute(cliente);
        pool.prestartAllCoreThreads();
        System.out.println("DEBUG - Cliente "+cliente.getId()+" mandato in esecuzione dalla classe Sportelli");
    }

    public void closeSportelli() {
        pool.shutdown();
        try {
            pool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getSizeCoda() {
        return coda.size();
    }

}
