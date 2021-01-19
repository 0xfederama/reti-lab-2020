import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

public class Power implements Callable<Double> {

    private final double base;
    private final double exp;

    public Power (double base, double exp) {
        this.base=base;
        this.exp=exp;
    }

    @Override
    public Double call() {
        System.out.println("Esecuzione " + base + "^" + exp + " in " + Thread.currentThread().getId());
        return Math.pow(base, exp);
    }
}

class MainPower {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("n = ");
        double base = scanner.nextDouble();
        scanner.close();
        double result = 0;

        ArrayList<Future<Double>> running = new ArrayList<Future<Double>>();
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (int i=2; i<=50; i++) {
            running.add(pool.submit(new Power(base, i)));
        }
        try {
            for (Future<Double> x : running) {
                result += x.get();
            }
            System.out.println("Result is " + result);
        } catch (ExecutionException exc) {
            System.out.println("Thread fail");
        } catch (InterruptedException exc) {
            System.out.println("Interrupted while waiting");
        } finally {
            pool.shutdown();
        }
    }
}
