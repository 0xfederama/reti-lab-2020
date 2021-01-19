import java.util.ArrayList;

public class consumerConti implements Runnable {

    private final ContoCorrente conto;
    private final Conti conti;

    public consumerConti(ContoCorrente conto, Conti conti) {
        this.conto = conto;
        this.conti = conti;
    }

    @Override
    public void run() {

        //System.out.println("CONSUMER: preso il conto #"+conto.getId()+" di "+conto.getNome());

        //Conto le occorrenze delle causali
        ArrayList<Movimento> movimenti = conto.getMovimenti();
        for (Movimento mov : movimenti) {
            String causale = mov.getCausale();
            conti.addOccorrenzaCausale(causale);
        }

        //System.out.println("CONSUMER: conto #"+conto.getId()+" di "+conto.getNome()+" analizzato");

    }
}
