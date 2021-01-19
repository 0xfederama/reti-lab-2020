import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) {

        //Server apre la connessione
        ServerSocketChannel serverSocketChannel;
        Selector selector;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            InetSocketAddress address = new InetSocketAddress(9999);
            serverSocket.bind(address);
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        System.out.println("Server ha aperto la connessione sulla porta 9999\n");
        int bufLen = 64; //Modificare la variabile per modificare la lunghezza del buffer

        while (true) {

            try {
                selector.select();
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();

                try {
                    if (key.isAcceptable()) {

                        //Accetto la connessione
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Nuova connessione con un client");
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        ByteBuffer output = ByteBuffer.allocate(4);
                        output.put(Integer.toString(bufLen).getBytes());
                        output.flip();
                        client.write(output);

                    } else if (key.isReadable()) {

                        //Controllo che il client sia ancora attivo, altrimenti esco
                        System.out.println("Key e' readable");
                        SocketChannel client = (SocketChannel) key.channel();

                        //Leggo dal client e aggiungo la risposa del server
                        String risposta = " - echoed by server\n";
                        ByteBuffer output = ByteBuffer.allocate(bufLen+risposta.length());
                        if (client.read(output) == -1) {
                            System.out.println("Connessione interrotta dal client");
                            key.cancel();
                            client.close();
                            continue;
                        }
                        output.put(risposta.getBytes());
                        output.flip();
                        key.interestOps(SelectionKey.OP_WRITE);
                        key.attach(output);

                    } else if (key.isWritable()) {

                        //Scrivo nel client la stringa firmata dal server e chiudo la connessione
                        System.out.println("Key e' writable");
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer input = (ByteBuffer) key.attachment();
                        client.write(input);
                        key.interestOps(SelectionKey.OP_READ);

                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                    System.out.println("Connessione interrotta con errore");
                }

            }

        }

    }
}
