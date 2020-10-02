import java.io.InputStreamReader;
import java.util.Scanner;
import java.lang.Thread;

public class main {
    public static void main(String[] args) {

        System.out.println("Inserisci i seguenti dati:");

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Accuracy = ");
        float accuracy = scanner.nextFloat();
        System.out.print("Tempo attesa (in msec) = ");
        int maxtime = scanner.nextInt();
        scanner.close();

        PiGregoryLeibniz PiCalc = new PiGregoryLeibniz(accuracy);
        Thread thread = new Thread(PiCalc);
        thread.start();
        try {
            thread.join(maxtime);
            if (thread.isAlive()) thread.interrupt();
        } catch (InterruptedException exc) {
            exc.printStackTrace();
        }

        System.out.println("Il valore di PI calcolato e' " + PiCalc.getPi());
    }
}
