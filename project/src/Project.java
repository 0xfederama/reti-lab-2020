import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Project {

    private String name;
    private String multicastAddress;
    private ArrayList<String> members;
    private ArrayList<String> cards;
    @JsonIgnore
    private ArrayList<Card> todo;
    @JsonIgnore
    private ArrayList<Card> inprogress;
    @JsonIgnore
    private ArrayList<Card> toberevised;
    @JsonIgnore
    private ArrayList<Card> done;
    private File backupDir;
    private ObjectMapper mapper;

    public Project(String name, String creator, String multicastAddress) {
        this.name = name;
        this.multicastAddress = multicastAddress;
        members = new ArrayList<>();
        cards = new ArrayList<>();
        todo = new ArrayList<>();
        inprogress = new ArrayList<>();
        toberevised = new ArrayList<>();
        done = new ArrayList<>();
        members.add(creator);
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        backupDir = new File("backup/"+this.name);
        if (!backupDir.exists()) {
            if (!backupDir.mkdir()) {
                System.out.println("Issues creating backup directory for the project");
            }
        }
    }

    public Project() {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
        cards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public Card getCard(String name) {
        //Search the card in every list
        if (!cards.contains(name)) return null;
        for (Card card : todo) {
            if (card.getName().equals(name)) {
                return card;
            }
        }
        for (Card card : inprogress) {
            if (card.getName().equals(name)) {
                return card;
            }
        }
        for (Card card : toberevised) {
            if (card.getName().equals(name)) {
                return card;
            }
        }
        for (Card card : done) {
            if (card.getName().equals(name)) {
                return card;
            }
        }
        return null;
    }

    @JsonIgnore
    public ArrayList<Card> getCardsCard() {
        //Get the cards list with type Card
        if (cards==null || cards.isEmpty()) {
            return null;
        }
        ArrayList<Card> allCards = new ArrayList<>();
        if (todo!=null && !todo.isEmpty()) {
            allCards.addAll(todo);
        }
        if (inprogress!=null && !inprogress.isEmpty()) {
            allCards.addAll(inprogress);
        }
        if (toberevised!=null && !toberevised.isEmpty()) {
            allCards.addAll(toberevised);
        }
        if (done!=null && !done.isEmpty()) {
            allCards.addAll(done);
        }
        return allCards;
    }

    public ArrayList<String> getCards() {
        //Get the cards list of type String
        return cards;
    }

    public boolean isMember (String username) {
        return members.contains(username);
    }

    public void addMember(String username) {
        if (!members.contains(username)) {
            members.add(username);
        }
    }

    public boolean addCard(String name, String description) {
        if (!cards.contains(name)) {
            Card card = new Card(name, description);
            todo.add(card);
            cards.add(name);
            File cardFile = new File("backup/"+this.name+"/"+name+".json");
            if (!cardFile.exists()) {
                try {
                    cardFile.createNewFile();
                } catch (IOException e) {
                    System.out.println("Issues creating backup file");
                }
            }
            try {
                mapper.writeValue(cardFile, card);
            } catch (IOException e) {
                System.out.println("IO exception writing to card file");
            }
            return true;
        } else {
            return false;
        }
    }

    public Card returnDeleteCard(ArrayList<Card> list, String name) {
        //Return and delete the card if found
        for (Card card : list) {
            if (card.getName().equals(name)) {
                list.remove(card);
                return card;
            }
        }
        return null;
    }

    public boolean moveCard(String name, String from, String to) {
        if (from.equals(to)) return true;
        if (!cards.contains(name)) return false;
        Card card = null;
        //Check if the move asked is possible
        switch(from) {
            case "todo": {
                if (!to.equals("inprogress")) return false;
                card = returnDeleteCard(todo, name);
                if (card==null) return false;
                card.addMovement(to);
                inprogress.add(card);
                break;
            }
            case "inprogress": {
                if (!to.equals("toberevised") && !to.equals("done")) return false;
                card = returnDeleteCard(inprogress, name);
                if (card==null) return false;
                if (to.equals("toberevised")) {
                    toberevised.add(card);
                } else { //to==done
                    done.add(card);
                }
                card.addMovement(to);
                break;
            }
            case "toberevised": {
                if (!to.equals("inprogress") && !to.equals("done")) return false;
                card = returnDeleteCard(toberevised, name);
                if (card==null) return false;
                if (to.equals("inprogress")) {
                    inprogress.add(card);
                } else { //to==done
                    done.add(card);
                }
                card.addMovement(to);
                break;
            }
            default: {
                return false;
            }
        }
        //Register the card to file
        File cardFile = new File("backup/"+this.name+"/"+name+".json");
        try {
            mapper.writeValue(cardFile, card);
        } catch (IOException e) {
            System.out.println("IO exception writing to card file");
        }
        return true;
    }

    @JsonIgnore
    public boolean isDeletable() {
        return todo.isEmpty() && inprogress.isEmpty() && toberevised.isEmpty();
    }

    //Method used to add old backupped cards to structures in memory
    public void addCard(Card card, String list) {
        if (todo==null) todo = new ArrayList<>();
        if (inprogress==null) inprogress = new ArrayList<>();
        if (toberevised==null) toberevised = new ArrayList<>();
        if (done==null) done = new ArrayList<>();
        if (list.equals("todo")) {
            todo.add(card);
        } else if (list.equals("inprogress")) {
            inprogress.add(card);
        } else if (list.equals("toberevised")) {
            toberevised.add(card);
        } else if (list.equals("done")) {
            done.add(card);
        }
    }

    //Getters and setters for jackson
    public void setName(String name) {
        this.name = name;
    }

    public void setMulticastAddress(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void setCards(ArrayList<String> cards) {
        this.cards = cards;
    }

    public void setTodo(ArrayList<Card> todo) {
        this.todo = todo;
    }

    public void setInprogress(ArrayList<Card> inprogress) {
        this.inprogress = inprogress;
    }

    public void setToberevised(ArrayList<Card> toberevised) {
        this.toberevised = toberevised;
    }

    public void setDone(ArrayList<Card> done) {
        this.done = done;
    }

}
