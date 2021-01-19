import java.io.File;

public class Producer extends Thread {

    private final Crawler crawler;
    private final File startDir;

    public Producer(Crawler crawler, File startDir) {
        this.crawler = crawler;
        this.startDir = startDir;
    }

    private void visitDir(File startDir) {
        File dir;
        if (startDir!=null && startDir.isDirectory()) {
            dir = startDir;
            File[] contents = dir.listFiles();
            for (File f : contents) {
                if (f.isDirectory()) {
                    File file = new File(f.getAbsolutePath());
                    crawler.insertDir(file);
                    visitDir(file);
                }
            }
        }
    }

    @Override
    public void run() {
        crawler.insertDir(startDir);
        visitDir(startDir);
        crawler.prodFinito();
        System.out.println("Producer [" + Thread.currentThread().getName() + "] finito");
    }

}
