//Es 2

import java.util.Calendar;

public class DatePrinterThread extends Thread {
    public static void main(String[] args) {
        Thread t = new DatePrinterThread();
        t.start();
        System.out.println("THREAD: " + Thread.currentThread().getName());
    }

    public void run () {
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
