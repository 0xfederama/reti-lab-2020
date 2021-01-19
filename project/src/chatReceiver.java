import java.io.IOException;
import java.net.*;

public class chatReceiver extends Thread {

    private final MainClient client;
    private final String projectname;
    private final String ipAddress;

    public chatReceiver(MainClient client, String projectname, String ipAddress) {
        this.client = client;
        this.projectname = projectname;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        //Create multicast socket
        MulticastSocket socket;
        try {
            socket = new MulticastSocket(8888);
            socket.joinGroup(InetAddress.getByName(ipAddress));
        } catch (IOException e) {
            System.out.println("IO exception joining socket group");
            return;
        }
        //Continue to receive messages while is not interrupted
        while (!currentThread().isInterrupted()) {
            try {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                if (!currentThread().isInterrupted()) {
                    String message = new String(packet.getData())+" project_"+projectname+"_EOS";
                    client.addMessage(message);
                }
            } catch (IOException e) {
                System.out.println("IO exception receiving message");
            }
        }

    }

}
