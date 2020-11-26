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
        SocketChannel server = SocketChannel.open(address);
        ByteBuffer msgLen = ByteBuffer.allocate(4);
        server.read(msgLen);
        msgLen.flip();
        int bufLen = Integer.parseInt(StandardCharsets.UTF_8.decode(msgLen).toString());

        while (true) {

            System.out.println("\nMessaggio #"+ i++);

            //Client legge la stringa da stdin
            String input;
            int len;
            do {
                System.out.print("Scrivere la stringa da inviare al server (massimo "+bufLen+" caratteri US-ASCII): ");
                input = scanner.nextLine();
                len = input.length();
                if (len > bufLen) System.out.println("La stringa inserita e' troppo lunga");
                if (len == 0) System.out.println("Stringa vuota non ammessa");
            } while (len > bufLen || len == 0);

            if (input.equals("$exit") || input.equals("$quit")) break;

            //Client manda la stringa al server e stampa la risposta
            try {
                //Scrivo in un buffer la stringa
                ByteBuffer buffer = ByteBuffer.allocate(len);
                buffer.clear();
                buffer.put(input.getBytes(StandardCharsets.US_ASCII)); //Per evitare errori nel caso vengano usate altre codifiche
                buffer.flip();

                //Invio il buffer al server
                server.write(buffer);
                System.out.println("Client ha inviato la stringa \""+input+"\" al server");

                //Leggo la risposta del server e chiudo la socket
                ByteBuffer buffer2 = ByteBuffer.allocate(len+" - echoed by server\n".length());
                server.read(buffer2);
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

        server.close();
        scanner.close();

    }

}
