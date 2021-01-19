import java.lang.Math;

public class PiGregoryLeibniz implements Runnable {

    private final float accuracy;
    private double pi=0;

    public PiGregoryLeibniz (float accuracy) {
        this.accuracy=accuracy;
    }

    @Override
    public void run() {
        float i=1;
        boolean somma=true;
        while (Math.abs(Math.PI - pi) > accuracy && !Thread.currentThread().isInterrupted()) {
            if (somma) {
                pi = pi + 4/i;
                somma=false;
            } else {
                pi = pi - 4/i;
                somma=true;
            }
            i+=2;
        }
    }

    public double getPi() {
        return pi;
    }

}
