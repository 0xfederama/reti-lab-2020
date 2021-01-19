import java.util.ArrayList;

public class Card {

    private String name;
    private String description;
    private String list;
    private ArrayList<String> history;

    public Card(String name, String description) {
        this.name = name;
        this.description = description;
        history = new ArrayList<>();
        history.add("todo");
        list = "todo";
    }

    public Card() {

    }

    //Add a movement to the the history
    public void addMovement(String to) {
        history.add(to);
        list = to;
    }

    public String getName() {
        return name;
    }

    public String getList() {
        return list;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHistory(ArrayList<String> history) {
        this.history = history;
    }

}
