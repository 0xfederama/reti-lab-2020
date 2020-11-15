import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server_esPreparazione {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6789);
        System.out.println("Server waiting for client");

        while(true) {
            Socket socket = serverSocket.accept();
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            File file = new File("test.txt");
            FileInputStream input = new FileInputStream("test.txt");
            byte[] fileByte = new byte[(int)file.length()];
            input.read(fileByte);
            output.write(fileByte, 0, (int)file.length());
            output.flush();
            socket.close();
        }
    }
}
