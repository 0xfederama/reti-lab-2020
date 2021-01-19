import java.util.Scanner;
import java.io.File;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Starting directory: ");
        String inputDir = scanner.nextLine();
        scanner.close();

        File startDir = new File(inputDir);
        if (!startDir.isDirectory()) {
            System.out.println(inputDir + " non e' una directory, esco");
            return;
        }
        Crawler crawler = new Crawler();

        int k = 5; //Numero di thread consumatori, modificabile ma sempre >0

        Thread producer = new Producer(crawler, startDir);
        Thread[] consumers = new Thread[k];
        for (int i=0; i<k; ++i) {
            consumers[i] = new Consumer(crawler);
        }
        producer.start();
        for (int i=0; i<k; ++i) {
            consumers[i].start();
        }

        producer.join();
        for (int i=0; i<k; ++i) {
            consumers[i].join();
        }

    }
}
