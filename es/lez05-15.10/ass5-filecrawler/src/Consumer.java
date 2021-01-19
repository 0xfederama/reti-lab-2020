public class Consumer extends Thread{

    private final Crawler crawler;

    public Consumer(Crawler crawler) {
        this.crawler = crawler;
    }

    @Override
    public void run() {
        while (true) {
            boolean cont = true;
            try {
                cont = crawler.stampaDir();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!cont) {
                break;
            }
        }
        System.out.println("Consumer [" + Thread.currentThread().getName() + "] finito");
    }

}
