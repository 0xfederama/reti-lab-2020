import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket listenSocket = new ServerSocket(6789);

        System.out.println("Server attivo sull'indirizzo 127.0.0.1:6789\n");

        while (true) {

            Socket connSocket = listenSocket.accept();
            System.out.println("Client" + connSocket.getInetAddress() + " connesso al server");

            BufferedReader readFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());

            String request = readFromClient.readLine();
            if (request==null) {
                System.out.println("Nessuna richiesta\n");
                connSocket.close();
                readFromClient.close();
                outToClient.close();
                continue;
            }
            System.out.println("Request: " + request);
            StringTokenizer tokenizer = new StringTokenizer(request);
            String httpMethod = tokenizer.nextToken();
            String httpQuery = tokenizer.nextToken();

            if (httpMethod.equals("GET")) {

                String fileName = httpQuery.replaceFirst("/", "");
                File file = new File(fileName);

                if (!file.exists() || !file.isFile()) {
                    outToClient.writeBytes("HTTP/1.0 404 Not Found\n");
                    String response = "<h1> ERROR 404 </h1> <b> <h4> File not found </h4>\n";
                    outToClient.writeBytes("Content-Type: text/html\n");
                    outToClient.writeBytes("Content-Length: " + response.length() + "\n");
                    outToClient.writeBytes("Connection: close\n\n");
                    outToClient.writeBytes(response);
                    System.out.println("File non trovato\n");
                    continue;
                }

                FileInputStream fin = new FileInputStream(fileName);
                String statusLine = "HTTP/1.0 200 OK\n";
                String contentTypeLine = "Content-Type: " + Files.probeContentType(Path.of(fileName)) + "\n";
                String contentLengthLine = "Content-Length: " + fin.available() + "\n";
                outToClient.writeBytes(statusLine);
                outToClient.writeBytes(contentTypeLine);
                outToClient.writeBytes(contentLengthLine);
                outToClient.writeBytes("Connection: close\n\n");

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fin.read(buffer)) != -1) {
                        outToClient.write(buffer, 0, bytesRead);
                }

                outToClient.flush();
                outToClient.close();
                fin.close();
                readFromClient.close();
                connSocket.close();
            }

            System.out.println("File trovato\n");

        }

    }
}
