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
        ServerSocketChannel serverChannel;
        Selector selector;
        try {
            serverChannel = ServerSocketChannel.open();
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(9999);
            ss.bind(address);
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        System.out.println("Server ha aperto la connessione sulla porta 9999");

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
                        System.out.println("Connessione accettata col client");
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);

                    } else if (key.isReadable()) {

                        //Leggo dal client e scrivo la nuova stringa
                        System.out.println("Key e' readable");
                        SocketChannel client = (SocketChannel) key.channel();
                        String risposta = " - echoed by server\n";
                        ByteBuffer output = ByteBuffer.allocate(64+risposta.length());
                        //Leggo dal client e aggiungo la risposa del server
                        client.read(output);
                        output.put(risposta.getBytes());
                        output.flip();
                        SelectionKey key1 = client.register(selector, SelectionKey.OP_WRITE);
                        key1.attach(output);

                    } else if (key.isWritable()) {

                        //Scrivo nel client la stringa firmata dal server e chiudo la connessione
                        System.out.println("Key e' writable");
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer input = (ByteBuffer) key.attachment();
                        client.write(input);
                        client.close();

                    }
                } catch (IOException e) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }
            }

        }

    }
}


































