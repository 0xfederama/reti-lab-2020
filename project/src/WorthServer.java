import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.util.*;

public class WorthServer extends RemoteServer implements ServerService {

    private ArrayList<Project> projects;
    private ArrayList<User> users;
    private HashMap<String, String> usersCallback;
    private final ArrayList<NotifyClientInterface> clients;
    private final File backupDir;
    private final File usersJson;
    private final File projectsJson;
    private final ObjectMapper mapper;
    private int lastMulticastAddress;

    public WorthServer() {
        super(); //For callbacks
        clients = new ArrayList<>();
        usersCallback = new HashMap<>();
        mapper = new ObjectMapper();
        lastMulticastAddress = -1;
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        backupDir = new File("backup");
        usersJson = new File("backup/users.json");
        projectsJson = new File("backup/projects.json");
    }

    public void start() {

        System.out.println("Starting server...");

        //Restore the previous backup from json files
        if (!restoreBackup()) {
            System.out.println("A problem occurred with backup, please make sure all the backup files are in the right place.");
            System.out.println("If you don't know how to fix it, simply delete the backup directory, the server with restart empty but without errors.");
            return;
        }

        //Setup TCP
        ServerSocketChannel serverSocketChannel;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            int ipPort = 22222;
            serverSocketChannel.socket().bind(new InetSocketAddress(ipPort));
            serverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            System.out.println("IO exception opening socket... quitting");
            e.printStackTrace();
            return;
        }
        //Setup selector
        Selector selector;
        try {
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            System.out.println("IO exception setting up selector... quitting");
            e.printStackTrace();
            return;
        }

        System.out.println("TCP and selector setup done");
        System.out.println("\nServer [online]");

        while (true) {
            try {
                selector.select();
            } catch (IOException e) {
                System.out.println("IO exception in the selector... quitting");
                e.printStackTrace();
                break;
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();

            while (iterator.hasNext()) {

                SelectionKey key = iterator.next();
                iterator.remove();

                try {

                    if (key.isAcceptable()) {

                        //Accept connection from client
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);

                    } else if (key.isReadable()) {

                        //Read command from client and execute it
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(128);
                        client.read(buffer);
                        buffer.flip();
                        String request = StandardCharsets.US_ASCII.decode(buffer).toString();
                        String[] command = request.split("\\s+");
                        //If client sent quit, remove from online users and quit
                        if (command[0].equals("quit")) {
                            key.channel().close();
                            key.cancel();
                            continue;
                        }
                        //Execute command and prepare answer for the client
                        String toClient = execCommand(command);
                        ByteBuffer answer = ByteBuffer.allocate(128);
                        answer.put(toClient.getBytes());
                        answer.flip();
                        key.interestOps(SelectionKey.OP_WRITE);
                        key.attach(answer);

                    } else if (key.isWritable()) {

                        //Send answer to the client
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer answer = (ByteBuffer) key.attachment();
                        client.write(answer);
                        key.interestOps(SelectionKey.OP_READ);
                        key.attach(null);

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

    //Backup methods for persistence
    private boolean restoreBackup() {

        if (backupDir.exists()) {
            System.out.println("Backup files found");
            //Restore backup from json files
            try {
                if (usersJson.length()!=0) {
                    users = new ArrayList<>(Arrays.asList(mapper.readValue(usersJson, User[].class)));
                } else {
                    users = new ArrayList<>();
                }
                if (projectsJson.length()!=0) {
                    projects = new ArrayList<>(Arrays.asList(mapper.readValue(projectsJson, Project[].class)));
                }
                if (users!=null) {
                    for (User user : users) {
                        usersCallback.put(user.getUsername(), "offline");
                    }
                }
                //Restore projects and cards
                if (projects!=null) {
                    for (Project project : projects) {
                        File projectDir = new File("backup/"+project.getName());
                        //Read cards json files
                        for (File cardFile : projectDir.listFiles()) {
                            try {
                                Card card = mapper.readValue(cardFile, Card.class);
                                project.addCard(card, card.getList());
                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("IO exception reading card");
                            }
                        }
                        //Restore last multicast address
                        String[] lastAddress = project.getMulticastAddress().split("\\.");
                        if (Integer.parseInt(lastAddress[3]) > lastMulticastAddress) {
                            lastMulticastAddress = Integer.parseInt(lastAddress[3]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("IO exception restoring backup");
                e.printStackTrace();
                return false;
            }
            System.out.println("Backup done");

        } else {

            //Create new backup files and start
            System.out.println("Backup files not found, starting empty");
            if (!backupDir.mkdir()) {
                System.out.println("Issues creating backup directory... quitting");
                return false;
            }
            try {
                usersJson.createNewFile();
                projectsJson.createNewFile();
            } catch (IOException e) {
                System.out.println("IO exception creating backup files... quitting");
                return false;
            }
            users = new ArrayList<>();
            projects = new ArrayList<>();
            System.out.println("Backup files created");
        }

        return true;
    }

    //Register with RMI
    public boolean register(String username, String password) throws RemoteException {
        for (User user : users) {
            if (user.getUsername().equals(username)) return false;
        }
        User user = new User(username, password);
        users.add(user);
        user.setStatus("offline");
        usersCallback.put(username, "offline");
        try {
            mapper.writeValue(usersJson, users);
        } catch (IOException e) {
            System.out.println("Error during backup, skipping backup");
        }
        return true;
    }

    //Execute commands sent/received with TCP
    private String execCommand(String[] command) {
        switch (command[0]) {
            case "login": {
                //If the username is not already assigned
                String username = command[1];
                String password = command[2];
                for (User user : users) {
                    if (username.equals(user.getUsername())) {
                        if (user.checkPassword(password)) {
                            user.setStatus("online");
                            usersCallback.replace(username, "online");
                            try {
                                update(command[1]);
                            } catch (RemoteException e) {
                                return "ERR - Remote exception updating callbacks";
                            }
                            return "User logged in";
                        } else {
                            return "ERR - Password is wrong";
                        }
                    }

                }
                return "ERR - That username does not exist";
            }
            case "logout": {
                //If the user exists
                String username = command[1];
                for (User user : users) {
                    if (username.equals(user.getUsername())) {
                        user.setStatus("offline");
                        usersCallback.replace(username, "offline");
                        try {
                            update(command[1]);
                        } catch (RemoteException e) {
                            return "ERR - Remote exception updating callbacks";
                        }
                        return "User logged out";
                    }
                }
                return "ERR - That username does not exist";
            }
            case "listprojects": {
                //List all the projects the user is a member of
                if (projects==null) {
                    return "No projects assigned to "+command[command.length-1];
                }
                String output = "Projects: ";
                for (Project project : projects) {
                    if (project.isMember(command[command.length-1])) {
                        output+=project.getName()+" ";
                    }
                }
                if (output.equals("Projects: ")) {
                    return "No projects assigned to "+command[command.length-1];
                }
                return output;
            }
            case "createproject": {
                //If the project doesn't exist
                if (command.length<3) return "Usage: createproject <projectname>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            return "You are already a member of the project";
                        } else {
                            return "A project with that name already exists";
                        }
                    }
                }
                //Assign a new address to the project
                String address = generateAddress();
                Project project = new Project(command[1], command[command.length-1], address);
                projects.add(project);
                try {
                    update(command[command.length-1]);
                } catch (RemoteException e) {
                    return "Remote exception updating callbacks";
                }
                try {
                    mapper.writeValue(projectsJson, projects);
                } catch (IOException e) {
                    System.out.println("Error during backup, skipping backup");
                }
                return "Project created";
            }
            case "addmember": {
                //If the user is a member and the new user exists
                if (command.length<4) return "Usage: addmember <projectname> <username>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            if (project.isMember((command[2]))) {
                                return command[2]+" is already a member of that project";
                            } else {
                                boolean exists = false;
                                for (User user : users) {
                                    if (user.getUsername().equals(command[2])) {
                                        exists=true;
                                        break;
                                    }
                                }
                                if (!exists) return "User '"+command[2]+"' does not exist";
                                project.addMember(command[2]);
                                try {
                                    update(command[2]);
                                } catch (RemoteException e) {
                                    return "Remote exception updating callbacks";
                                }
                                try {
                                    mapper.writeValue(projectsJson, projects);
                                } catch (IOException e) {
                                    System.out.println("Error during backup, skipping backup");
                                }
                                return command[2]+" is now a member of the project";
                            }
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "showmembers": {
                //If the project exists and the user is a member
                if (command.length<3) return "Usage: showmembers <projectname>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            return project.getMembers().toString();
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "showcards": {
                //If the project exists, there are cards and the user is a member
                if (command.length<3) return "Usage: showcards <projectname>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            if (project.getCardsCard()==null) {
                                return "There are no cards in this project";
                            }
                            String output = "Cards:\n";
                            ArrayList<Card> cards = project.getCardsCard();
                            for (int i=0; i<cards.size(); ++i) {
                                Card card = cards.get(i);
                                if (i == cards.size()-1) { //Insert last element without newline
                                    output+="  list="+card.getList()+", name="+card.getName();
                                } else {
                                    output+="  list="+card.getList()+", name="+card.getName()+"\n";
                                }
                            }
                            return output; //I'm sure that this is not null thanks to the if above
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "showcard": {
                //If card and project exist and the user is a member
                if (command.length<4) return "Usage: showcard <projectname> <cardname>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            Card card = project.getCard(command[2]);
                            if (card!=null) {
                                return "Name: "+card.getName()+", description: "+card.getDescription()+", list: "+card.getList();
                            } else {
                                return "That card doesn't exist";
                            }
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "addcard": {
                //If the project exists, there are no cards with the name of the new card and if the user is a member
                if (command.length<5) return "Usage: addcard <projectname> <cardname> <description>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            if (!project.addCard(command[2], command[3])) {
                                return "A card with that name already exists";
                            } else {
                                try {
                                    mapper.writeValue(projectsJson, projects);
                                } catch (IOException e) {
                                    System.out.println("Error during backup, skipping backup");
                                }
                                return "Card successfully added to the project";
                            }
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "movecard": {
                //If card and project exist and the user is a member
                if (command.length<6) return "Usage: movecard <projectname> <cardname> <list1> <list2>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            if (!project.moveCard(command[2], command[3], command[4])) {
                                return "An error occurred moving the card, please make sure that you specified cardname and lists correctly";
                            } else {
                                try {
                                    mapper.writeValue(projectsJson, projects);
                                } catch (IOException e) {
                                    System.out.println("Error during backup, skipping backup");
                                }
                                return "Card moved successfully from "+command[3]+" to "+command[4];
                            }
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "getcardhistory": {
                //If card and project exist and the user is a member
                if (command.length<4) return "Usage: getcardhistory <projectname> <cardname>";
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            Card card = project.getCard(command[2]);
                            if (card!=null) {
                                return "History: "+card.getHistory().toString();
                            } else {
                                return "That card doesn't exist";
                            }
                        }
                    }
                }
                return "That project does not exist or you are not listed as a member";
            }
            case "cancelproject": {
                //If project exists and the user is a member
                int i=0;
                if (projects==null) {
                    projects = new ArrayList<>();
                }
                for (Project project : projects) {
                    if (project.getName().equals(command[1])) {
                        if (project.isMember(command[command.length-1])) {
                            if (project.isDeletable()) {
                                projects.remove(i);
                                try {
                                    update(command[2]);
                                } catch (RemoteException e) {
                                    return "ERR - Remote exception updating callbacks";
                                }
                                try {
                                    mapper.writeValue(projectsJson, projects);
                                } catch (IOException e) {
                                    System.out.println("Error during backup, skipping backup");
                                }
                                //Delete project directory
                                File projectDir = new File("backup/"+project.getName());
                                File[] contents = projectDir.listFiles();
                                if (contents != null) {
                                    for (File file : contents) {
                                        file.delete();
                                    }
                                }
                                projectDir.delete();
                                return "Project deleted successfully";
                            } else {
                                return "In order to delete a project, all cards must be listed as done";
                            }
                        }
                    }
                    i++;
                }
                return "That project does not exist or you are not listed as a member";
            }
            default: {
                return "Unsupported operation, type 'help' to show the commands available";
            }
        }
    }

    //Support method to generate a multicast ip address for the chat
    private String generateAddress() {
        int lastDigitsIP = ++lastMulticastAddress;
        return "224.0.0."+lastDigitsIP;
    }

    //RMI Callbacks methods
    public synchronized void registerForCallbacks(NotifyClientInterface clientInterface) {
        if (!clients.contains(clientInterface)) {
            clients.add(clientInterface);
            System.out.println("New client registered for callbacks");
        }
    }

    public synchronized void unregisterForCallbacks(NotifyClientInterface clientInterface) {
        if (clients.remove(clientInterface)) {
            System.out.println("Client unregistered from callbacks");
        } else {
            System.out.println("Unable to unregister client");
        }
    }

    public void update(String username) throws RemoteException {
        doCallbacks(username);
        System.out.println("Callbacks update done");
    }

    private synchronized void doCallbacks(String username) throws RemoteException {
        NotifyClientInterface toRemove = null; //Save the client to be removed if the client closed without logging out and unregistering for callbacks
        for (NotifyClientInterface client : clients) {
            try {
                //Notify users list and, if not empty, the projects list
                client.notifyUsers(usersCallback);
                if (projects==null) {
                    client.notifySockets(null);
                } else {
                    //Create a new hashmap to pass to the client
                    HashMap<String, String> sockets = new HashMap<>();
                    for (Project project : projects) {
                        if (project.isMember(username)) {
                            sockets.put(project.getName(), project.getMulticastAddress());
                        }
                    }
                    client.notifySockets(sockets);
                }
            } catch (ConnectException e) {
                toRemove=client;
            }
        }
        if (toRemove!=null) {
            unregisterForCallbacks(toRemove);
        }
    }

}
