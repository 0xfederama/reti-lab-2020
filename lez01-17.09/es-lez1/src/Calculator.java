public class Calculator extends Thread { //oppure implements Runnable
    private int number;
    public Calculator(int number) {
        this.number=number;
    }
    public void run() {
        for (int i=1; i<=10; i++){
            System.out.printf("%s: %d * %d = %d\n", Thread.currentThread().getName(),number,i,i*number);
        }
    }
}

class main {
    public static void main(String[] args) {
        for (int i = 0; i <= 10; i++) {
            Calculator calculator = new Calculator(i);
            calculator.start();
            /* Posso fare anche
            Thread thread=new Thread(calculator);
            thread.start();
             */
        }
        System.out.println("Avviato Calcolo Tabelline");
    }
}