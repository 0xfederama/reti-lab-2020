import java.util.LinkedList;
import java.io.File;

public class Crawler {

    private final LinkedList<File> listaDir;
    private boolean producerFinished;

    public Crawler() {
        listaDir = new LinkedList<>();
        producerFinished = false;
    }

    public void prodFinito() {
        producerFinished = true;
        synchronized (listaDir) {
            listaDir.notifyAll();
        }
    }

    public void insertDir(File dir) {
        synchronized (listaDir) {
            listaDir.add(dir);
            listaDir.notifyAll();
        }
        System.out.println("Producer ha aggiunto alla coda la DIRECTORY " + dir.getName());
    }

    public boolean stampaDir() throws InterruptedException {
        File dir;
        synchronized (listaDir) {
            while (listaDir.size()==0 && !producerFinished) {
                listaDir.wait();
            }
            if (producerFinished && listaDir.size()==0) return false;
            dir = listaDir.poll();
        }
        System.out.println("Consumer ha prelevato dalla coda la DIRECTORY " + dir.getName());
        File[] contents = dir.listFiles();
        for (File f : contents) {
            if (f.isDirectory()) System.out.println("Consumer - DIRECTORY " + f.getName());
            else System.out.println("Consumer - FILE " + f.getName());
        }
        return true;
    }

}
