import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Quanti messaggi inviare al server? ");
        int num = scanner.nextInt();
        scanner.nextLine();

        for (int i=0; i<num; ++i) {
            System.out.println("\nMessaggio #"+i);

            //Client legge la stringa da stdin
            String input;
            do {
                System.out.println("Scrivere la stringa da inviare al server (massimo 64 caratteri):");
                input = scanner.nextLine();
                if (input.length() > 64) System.out.println("La stringa inserita e' troppo lunga");
            } while (input.length() > 64);

            //Client manda la stringa al server e stampa la risposta
            try {

                //Apro la connessione
                SocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
                SocketChannel client = SocketChannel.open(address);

                //Scrivo in un buffer
                ByteBuffer buffer = ByteBuffer.allocate(64);
                buffer.clear();
                buffer.put(input.getBytes());
                buffer.flip();

                //Invio al server
                client.write(buffer);
                System.out.println("Client ha inviato la stringa "+input+" al server");

                //Leggo la risposta del server
                ByteBuffer buffer2 = ByteBuffer.allocate(64+" - echoed by server\n".length());
                client.read(buffer2);
                buffer2.flip();
                String s = "";
                while (buffer2.hasRemaining()) {
                    s+=StandardCharsets.UTF_8.decode(buffer2).toString();
                }
                System.out.print("Server risponde " +s);
                client.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        scanner.close();

    }

}
