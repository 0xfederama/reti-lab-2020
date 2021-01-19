public class TaskCliente implements Runnable {

    private int id;
    public TaskCliente(int id) { this.id = id; }

    @Override
    public void run() {
        int time = (int)(Math.random() * 1000);
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Cliente "+id+" e' stato servito in "+time+"msec");
    }

    public int getId() {
        return id;
    }
}
