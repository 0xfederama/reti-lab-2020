public class Dropbox {

    private int num;
    private boolean full = false;

    public synchronized int take(boolean pari) {
        boolean ok;
        ok = pari==(num%2==0);
        while (!full || !ok) {
            try {
                wait();
                ok = pari==(num%2==0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(num + " preso con take()");
        full = false;
        notifyAll();
        return num;
    }

    public synchronized void put(int insert) {
        while (full) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(insert + " inserito con put()");
        num=insert;
        full = true;
        notifyAll();
    }

}
