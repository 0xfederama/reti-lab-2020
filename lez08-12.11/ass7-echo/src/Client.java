import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Inserire le stringhe da inviare al server. Usare \"$exit\" o \"$quit\" per uscire.");
        int i=0;

        //Apro la connessione
        SocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
        SocketChannel client = SocketChannel.open(address);

        while (true) {

            System.out.println("\nMessaggio #"+ i++);

            //Client legge la stringa da stdin
            String input;
            do {
                System.out.print("Scrivere la stringa da inviare al server (massimo 128 caratteri): ");
                input = scanner.nextLine();
                if (input.length() > 128) System.out.println("La stringa inserita e' troppo lunga");
                if (input.length() == 0) System.out.println("Stringa vuota non ammessa");
            } while (input.length() > 128 || input.length() == 0);

            if (input.equals("$exit") || input.equals("$quit")) break;

            //Client manda la stringa al server e stampa la risposta
            try {
                //Scrivo in un buffer la stringa
                ByteBuffer buffer = ByteBuffer.allocate(128);
                buffer.clear();
                buffer.put(input.getBytes());
                buffer.flip();

                //Invio il buffer al server
                client.write(buffer);
                System.out.println("Client ha inviato la stringa \""+input+"\" al server");

                //Leggo la risposta del server e chiudo la socket
                ByteBuffer buffer2 = ByteBuffer.allocate(128+" - echoed by server\n".length());
                client.read(buffer2);
                buffer2.flip();
                String s = "";
                while (buffer2.hasRemaining()) {
                    s+=StandardCharsets.UTF_8.decode(buffer2).toString();
                }
                System.out.print("Server risponde: "+s);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        client.close();
        scanner.close();

    }

}
