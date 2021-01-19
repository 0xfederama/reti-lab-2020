//Es 1

import java.util.Calendar;

public class DatePrinter {
    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println("Thread: " + Thread.currentThread().getName() + "\n Date: " + Calendar.getInstance().getTime());
            Thread.sleep(2000);
        }
        //System.out.println(Thread.currentThread().getName());
    }
}
