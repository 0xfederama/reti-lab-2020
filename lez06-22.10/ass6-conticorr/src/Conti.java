import java.util.ArrayList;

public class Conti {

    public ArrayList<ContoCorrente> conti;
    private int nBon;
    private int nAcc;
    private int nBoll;
    private int nF24;
    private int nPagoB;

    public Conti(int nConti) {
        conti = new ArrayList<>(nConti);
        nBon=0;
        nAcc=0;
        nBoll=0;
        nF24=0;
        nPagoB=0;
    }

    public Conti() {

    }

    public void addConto(ContoCorrente conto) {
        conti.add(conto);
    }

    public ArrayList<ContoCorrente> getConti() {
        return conti;
    }

    synchronized public void addOccorrenzaCausale(String causale) {
        switch (causale) {
            case "Bonifico":
                nBon++;
                break;
            case "Accredito":
                nAcc++;
                break;
            case "Bollettino":
                nBoll++;
                break;
            case "F24":
                nF24++;
                break;
            case "PagoBancomat":
                nPagoB++;
                break;
        }
    }

    public void stampaOcc() {
        System.out.println("\nLe occorrenze delle causali sono:");
        System.out.println("#Bonifici = "+nBon);
        System.out.println("#Accrediti = "+nAcc);
        System.out.println("#Bollettini = "+nBoll);
        System.out.println("#F24 = "+nF24);
        System.out.println("#PagoBancomat = "+nPagoB);
    }

}
