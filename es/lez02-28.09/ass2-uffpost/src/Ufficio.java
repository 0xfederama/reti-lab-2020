import java.io.InputStreamReader;
import java.util.*;

public class Ufficio {
    public static void main(String[] args) {

        System.out.print("Quante persone possono entrare nella sala d'attesa piccola? ");
        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        int dim = scanner.nextInt();
        System.out.print("Quanti clienti entrano? ");
        int nClienti = scanner.nextInt();
        scanner.close();

        //Creo la coda della sala d'attesa piu' grande (dim=inf) e faccio entrare nClienti clienti
        Queue<TaskCliente> SalaGrande = new LinkedList<>();
        for (int i=0; i<nClienti; ++i) {
            TaskCliente cliente = new TaskCliente(i);
            SalaGrande.add(cliente);
            System.out.println("DEBUG - Cliente "+i+" entrato nella sala grande");
        }

        //Creo il thread pool per gli sportelli
        Sportelli sportelli = new Sportelli(dim);

        //Faccio entrare i clienti nell'altra sala fino a che non finiscono, ovvero attivo i task
        //  clienti nel thread pool degli sportelli prendendoli dalla lista di clienti nella sala grande
        while (SalaGrande.size() > 0) {
            TaskCliente cliente = SalaGrande.poll();
            while (sportelli.getSizeCoda() >= dim) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            sportelli.executeTask(cliente);
            System.out.println("DEBUG - Cliente "+cliente.getId()+" entrato nella sala piccola");
        }

        System.out.println("DEBUG -Tutti i clienti sono entrati, chiudo l'ufficio");
        sportelli.closeSportelli();
    }
}
