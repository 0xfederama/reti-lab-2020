import java.time.LocalDate;

public class Movimento {

    private int id;
    private String data;
    private String causale;
    private int importo;

    public Movimento(int id) {
        this.id=id;
        this.data=randomDate().toString();
        int caus = (int) (Math.random()*5 + 1);
        switch (caus) {
            case 1: causale = "Bonifico";
                break;
            case 2: causale = "Accredito";
                break;
            case 3: causale = "Bollettino";
                break;
            case 4: causale = "F24";
                break;
            case 5: causale = "PagoBancomat";
                break;
        }
        importo = (int) (Math.random()*10000 - 5000); //Range dell'operazione [-5000, 5000]
    }

    public Movimento() {

    }

    private LocalDate randomDate() {
        int oggi = 2 * 365; //20 anni dall'inizio
        int inizio = 48 * 365; //movimenti registrati relativi agli ultimi due anni
        return LocalDate.ofEpochDay((long)(Math.random()*oggi + inizio));
    }

    public int getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getCausale() {
        return causale;
    }

    public int getImporto() {
        return importo;
    }

}
