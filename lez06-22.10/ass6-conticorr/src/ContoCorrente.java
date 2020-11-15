import java.util.ArrayList;

public class ContoCorrente {

    private int id;
    private String nome;
    private final ArrayList<Movimento> movimenti = new ArrayList<>();

    public ContoCorrente(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ContoCorrente() {

    }

    public void addMovimenti(int num) {
        for (int i=0; i<num; ++i) {
            Movimento mov = new Movimento(i);
            movimenti.add(mov);
        }
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public ArrayList<Movimento> getMovimenti() {
        return movimenti;
    }

}
