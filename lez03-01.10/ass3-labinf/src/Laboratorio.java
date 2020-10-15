import java.io.InputStreamReader;
import java.util.*;

public class Laboratorio {

    public static void main(String[] args) throws InterruptedException {

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Quanti professori entrano? ");
        int nProf = scanner.nextInt();
        System.out.print("Quanti tesisti entrano? ");
        int nTes = scanner.nextInt();
        System.out.print("Quanti studenti entrano? ");
        int nStud = scanner.nextInt();
        scanner.close();

        int utentiTot = nProf + nTes + nStud;

        //Creo il laboratorio
        Computers pc = new Computers();
        //Creo la lista di thread utenti
        Vector<Thread> threadsUtente = new Vector<>(utentiTot);

        //Avvio un prof/tesista/studente alla volta
        System.out.println("Faccio entrare gli utenti");
        final long startTime = System.nanoTime();
        int i=0;
        while (nProf>0 || nTes>0 || nStud>0) {

            if (nProf>0) {
                //Professore si mette in coda
                Thread utente = new Thread (new Utente("Professore", pc, i));
                threadsUtente.add(utente);
                nProf--;
            }
            if (nTes>0) {
                //Tesista si mette in coda
                Thread utente = new Thread (new Utente("Tesista", pc, i));
                threadsUtente.add(utente);
                nTes--;
            }
            if (nStud>0) {
                //Studente si mette in coda
                Thread utente = new Thread(new Utente("Studente", pc, i));
                threadsUtente.add(utente);
                nStud--;
            }
            i++;
        }

        System.out.println("Attivo i thread utente");
        for (i=0; i<utentiTot; ++i) {
            threadsUtente.get(i).start();
        }

        System.out.println("Thread utente attivati, aspetto che finiscano");
        for (i=0; i<utentiTot; ++i) {
            threadsUtente.get(i).join();
        }

        final long endTime = System.nanoTime();
        long time = endTime-startTime;

        //Sono entrati e usciti tutti
        System.out.println("\nLaboratorio aperto per "+time+"ms.\nTutti gli utenti sono entrati e usciti, chiudo il laboratorio");
        
    }
}
