//Es 3

import java.util.Calendar;

public class DatePrinterRunnable implements Runnable {
    public static void main(String[] args) {
        DatePrinterRunnable print = new DatePrinterRunnable();
        Thread t = new Thread(print);
        t.start();
        System.out.println("THREAD: " + Thread.currentThread().getName());
    }

    public void run() {
        while (true) {
            System.out.println("THREAD: " + Thread.currentThread().getName() + "    DATE: " + Calendar.getInstance().getTime());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
