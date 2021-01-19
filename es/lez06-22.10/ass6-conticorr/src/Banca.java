import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Banca {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        Scanner scanner = new Scanner(new InputStreamReader(System.in));
        System.out.print("Quanti conti correnti registrati ha la banca? ");
        final int nContiCorr = scanner.nextInt();
        if (nContiCorr<1) {
            System.out.println("I conti correnti registrati devono essere almeno uno");
            return;
        }
        scanner.close();

        String[] arrayNomi = {"Federico","Luca","Linda","Tommaso","Mario","Silvia","Gaia","Laura","Alice","Sara","Marco",
                                "Ruslan","Matteo","Leonardo","Giulia","Andrea","Filippo","Francesco","Sabrina","Nicole"};

        Conti conti = new Conti(nContiCorr);

        //Aggiungo alla classe conti tutti i conti correnti che devono essere creati
        for (int i=0; i<nContiCorr; ++i) {

            String cognome;
            if (i%2 == 0) cognome="Bianchi"; else cognome="Rossi"; //Per avere il doppio dei nomi possibili
            String nomeCompleto = arrayNomi[(int) (Math.random() * arrayNomi.length)] + " " + cognome;

            //Creo il conto, aggiungo movimenti (random tra 1 e 50) e aggiungo il conto alla lista dei conti
            ContoCorrente conto = new ContoCorrente(i, nomeCompleto);
            conto.addMovimenti((int) (Math.random()*50 + 1));
            conti.addConto(conto);

        }

        System.out.println("\nHo creato tutti gli utenti della banca, ora scrivo nel file json");

        //Scrivo nel file json i vari conti correnti usando un try-with-resources
        try (   FileOutputStream fOut = new FileOutputStream("conti.json");
                FileChannel channelOut = fOut.getChannel() ) {

            //Creo un objectMapper in cui inserisco tutti i conti, trasformo in stringa e poi in byte per scriverla con NIO
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(conti);
            byte[] sByte = s.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(sByte.length);

            buffer.put(sByte);
            buffer.flip();
            channelOut.write(buffer);
            buffer.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Ho scritto correttamente nel file json, ora attivo i thread");

        //Creo un thread che legge i conti e thread che li analizzano
        Thread producer = new producerConti(conti, "conti.json");
        producer.start();

        System.out.println("Thread producer partito");

        //Aspetto il thread producer
        producer.join();

        //Stampo tutte le occorrenze delle causali
        conti.stampaOcc();

    }
}
