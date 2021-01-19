import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MainClient extends RemoteObject implements NotifyClientInterface {

    private SocketChannel socketChannel;
    private HashMap<String, String> usersCallback; //List of users updated by callbacks
    private final HashMap<String, String> projectMulticast; //List of chat sockets updated by callbacks
    private final ArrayList<String> messages;
    private final ArrayList<Thread> chatThreads;
    private String username;

    public MainClient() {
        super();
        usersCallback = null;
        username = "---";
        projectMulticast = new HashMap<>();
        messages = new ArrayList<>();
        chatThreads = new ArrayList<>();
    }

    public void start() {

        try {

            final int rmiPort = 11111;
            final int ipPort = 22222;
            final String ipAddress = "127.0.0.1";

            //Setup RMI and callbacks
            Registry registry;
            ServerService stub;
            NotifyClientInterface callbackObj;
            NotifyClientInterface stubCallback;
            try {
                registry = LocateRegistry.getRegistry(rmiPort);
                stub = (ServerService) registry.lookup("ServerRMI");
                callbackObj = this;
                stubCallback = (NotifyClientInterface) UnicastRemoteObject.exportObject(callbackObj, 0);
            } catch (NotBoundException e) {
                System.out.println("Remote expection connecting to RMI, quitting");
                return;
            }

            //Setup TCP
            try {
                socketChannel = SocketChannel.open();
                socketChannel.connect(new InetSocketAddress(ipAddress, ipPort));
            } catch (ClosedChannelException e) {
                System.out.println("IO Exception opening socket... quitting");
                e.printStackTrace();
                return;
            }

            //Read commands from command line input
            Scanner scanner = new Scanner(System.in);
            boolean loggedIn = false;
            boolean quit = false;
            System.out.println("Welcome to WORTH!\n");
            help();
            System.out.println();
            while (!quit) {
                //Read input
                System.out.print("$> ");
                String input = scanner.nextLine() + " " + username;
                String[] command = input.split("\\s+");
                if (command[0].isEmpty()) {
                    System.out.println("No commands entered\n");
                    continue;
                }
                switch (command[0]) {
                    case "register": {
                        //Register user with rmi
                        if (loggedIn) {
                            System.out.println("You are already logged in, logout before registering");
                            break;
                        }
                        if (command.length < 4) {
                            System.out.println("Usage: register <username> <password>");
                            break;
                        }
                        if (command[1].equals(command[2])) {
                            System.out.println("Username and password must be different");
                            break;
                        }
                        if (!stub.register(command[1], command[2])) {
                            System.out.println("The username is already taken");
                        } else {
                            System.out.println("User " + command[1] + " registered successfully");
                        }

                        break;
                    }
                    case "login": {
                        if (loggedIn) {
                            System.out.println("You are already logged in");
                            break;
                        }
                        if (command.length < 4) {
                            System.out.println("Usage: login <username> <password>");
                            break;
                        }
                        stub.registerForCallbacks(stubCallback);
                        String received = sendCommand(input);
                        System.out.println(received);
                        if (!received.contains("ERR")) {
                            loggedIn = true;
                            username = command[1];
                        } else {
                            stub.unregisterForCallbacks(stubCallback);
                        }
                        break;
                    }
                    case "logout": {
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        if (command.length < 3) {
                            System.out.println("Usage: logout <username>");
                            break;
                        }
                        if (!username.equals(command[1])) {
                            System.out.println("You are trying to logout from another user");
                            break;
                        }
                        String received = sendCommand(input);
                        System.out.println(received);
                        if (!received.contains("ERR")) {
                            loggedIn = false;
                            //Unregister for callbacks
                            stub.unregisterForCallbacks(stubCallback);
                            //Shut down all threads and clear messages
                            for (Thread thread : chatThreads) {
                                thread.interrupt();
                            }
                            chatThreads.clear();
                            synchronized (messages) {
                                messages.clear();
                            }
                            projectMulticast.clear();
                        }
                        break;
                    }
                    case "listusers": {
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        if (usersCallback == null) {
                            System.out.println("Users list is empty");
                            break;
                        }
                        for (String key : usersCallback.keySet()) {
                            System.out.println(key + " - " + usersCallback.get(key));
                        }
                        break;
                    }
                    case "listonlineusers": {
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        if (usersCallback == null) {
                            System.out.println("Users list is empty");
                            break;
                        }
                        for (String key : usersCallback.keySet()) {
                            if (usersCallback.get(key).equals("online")) {
                                System.out.println(key + " - " + usersCallback.get(key));
                            }
                        }
                        break;
                    }
                    case "readchat": {
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        if (command.length < 3) {
                            System.out.println("Usage: readchat <projectname>");
                            break;
                        }
                        //Check if the projects exists locally
                        String address = projectMulticast.get(command[1]);
                        if (address == null) {
                            System.out.println("That project does not exist or you are not a member");
                            break;
                        }
                        synchronized (messages) {
                            //Read messages in the list
                            int numMessages = 0;
                            ArrayList<String> toRemove = new ArrayList<>();
                            for (String message : messages) {
                                if (message.contains("project_" + command[1] + "_EOS") && !message.contains(username + ":")) {
                                    String[] messageOnly = message.split("project_");
                                    System.out.println(messageOnly[0]);
                                    numMessages++;
                                    toRemove.add(message);
                                }
                            }
                            //Remove read messages
                            if (!toRemove.isEmpty()) {
                                for (int i = 0; i < numMessages; ++i) {
                                    messages.remove(toRemove.get(i));
                                }
                            } else {
                                System.out.println("No messages received or you are not a member of the project");
                            }

                        }
                        break;
                    }
                    case "sendchatmsg": {
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        if (command.length < 4) {
                            System.out.println("Usage: sendchatmsg <projectname> \"message\"");
                            break;
                        }
                        //Check if the message is between two ""
                        int numMarks = 0;
                        for (char c : input.toCharArray()) {
                            if (c == '\"') numMarks++;
                        }
                        if (numMarks < 2) {
                            System.out.println("The message must be written between two \"");
                            break;
                        }
                        //Get chat address
                        String address = projectMulticast.get(command[1]);
                        if (address == null) {
                            System.out.println("That project does not exist or you are not a member");
                            break;
                        }
                        //Find and send message via UDP multicast
                        String[] findMessage = input.split("\"");
                        String message = username + ": " + findMessage[1];
                        if (findMessage[1].equals("")) {
                            System.out.println("Warning: you are sending an empty message");
                        }
                        try {
                            InetAddress ip = InetAddress.getByName(address);
                            byte[] tosend = message.getBytes();
                            DatagramPacket packet = new DatagramPacket(tosend, tosend.length, ip, 8888);
                            int sendPort = ThreadLocalRandom.current().nextInt(1024, 65535);
                            DatagramSocket socket = new DatagramSocket(sendPort);
                            socket.setSoTimeout(300000); //5 minutes
                            socket.send(packet);
                            System.out.println("Message sent");
                        } catch (IOException e) {
                            System.out.println("IO exception sending message to chat");
                            break;
                        }
                        break;
                    }
                    case "cancelproject": {
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        if (command.length < 3) {
                            System.out.println("Usage: cancelproject <projectname>");
                            break;
                        }
                        //If received=="" the server is closed
                        String received = sendCommand(input);
                        if (received.equals("")) {
                            throw new IOException();
                        }
                        if (!received.contains("ERR")) {
                            projectMulticast.remove(command[1]);
                        }
                        System.out.println(received);
                        break;
                    }
                    case "help": {
                        help();
                        break;
                    }
                    case "quit": {
                        if (loggedIn) {
                            System.out.println("You need to logout before quitting");
                        } else {
                            sendCommand(input);
                            quit = true;
                        }
                        break;
                    }
                    default: {
                        //Send all the other commands directly to the server.
                        if (!loggedIn) {
                            System.out.println("You are not logged in");
                            break;
                        }
                        //If received=="" the server is closed
                        String received = sendCommand(input);
                        if (received.equals("")) {
                            throw new IOException();
                        }
                        System.out.println(received);
                        break;
                    }

                }
                System.out.println();
            }

            System.out.println("Client closed");
            System.exit(0);

        } catch (ConnectException e) {
            System.out.println("Server offline, quitting");
            System.exit(0);
        } catch (RemoteException e) {
            System.out.println("Remote exception, quitting");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Server offline, quitting");
            System.exit(0);
        }

    }

    private void help() {
        System.out.println("These are the commands you can use:");
        System.out.println("    register <username> <password>");
        System.out.println("    login <username> <password>");
        System.out.println("    logout <username>");
        System.out.println("    listusers - show the list of all the users registered to the service");
        System.out.println("    listonlineusers - show the list of all the online users registered to the service");
        System.out.println("    listprojects - show the list of all the projects assigned to the current user");
        System.out.println("    createproject <projectname> - create a new project");
        System.out.println("    addmember <projectname> <username> - add a member to a project");
        System.out.println("    showmembers <projectname> - show the members of a project");
        System.out.println("    showcards <projectname> - show all the cards inside a project");
        System.out.println("    showcard <projectname> <cardname> - show title, description and list of a card in a project");
        System.out.println("    addcard <projectname> <cardname> <description> - add a card to a project");
        System.out.println("    movecard <projectname> <cardname> <list1> <list2> - move a card from list1 to list2");
        System.out.println("    getcardhistory <projectname> <cardname> - show list history of a card in a project");
        System.out.println("    readchat <projectname> - read all the received and sent messages of a project chat");
        System.out.println("    sendchatmsg <projectname> \"message\" - send a message to a project chat");
        System.out.println("    cancelproject <projectname> - delete a project");
        System.out.println("    help - display this message of help");
        System.out.println("    quit - close the program");
        System.out.println("    WARNING: no spaces allowed in the parameters of the commands (neither in the card description)");
        System.out.println("    WARNING: no spaces allowed before a command");
        System.out.println("    WARNING: additional parameters will not be considered");
        System.out.println("    WARNING: the message of sendchatmsg must be written between two inverted commas (\")");
        System.out.println("    WARNING: only US-ASCII characters allowed");
    }

    //Send command with TCP to the server
    private String sendCommand(String command) throws IOException {

        //Put string in a bytebuffer
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.clear();
        buffer.put(command.getBytes(StandardCharsets.US_ASCII));
        buffer.flip();

        //Send buffer to server
        socketChannel.write(buffer);
        //Read answer from server
        ByteBuffer buffer1 = ByteBuffer.allocate(128);
        socketChannel.read(buffer1);
        buffer1.flip();
        return StandardCharsets.US_ASCII.decode(buffer1).toString();

    }

    //Callback notification methods
    public void notifyUsers(HashMap<String, String> users) {
        usersCallback = users;
    }

    public void notifySockets(HashMap<String, String> sockets) {
        //Launch a thread for each new project
        if (sockets==null) return;
        for (String project : sockets.keySet()) {
            if (!projectMulticast.containsKey(project)) {
                //Launch new thread
                Thread listener = new Thread(new chatReceiver(this, project, sockets.get(project)));
                chatThreads.add(listener);
                projectMulticast.put(project, sockets.get(project));
                listener.start();
            }
        }
    }

    //Message update method
    public void addMessage(String message) {
        synchronized (messages) {
            messages.add(message);
        }
    }

    public static void main(String[] args) {
        MainClient client = new MainClient();
        client.start();
    }

}
