package Ballot;
public class Option {
    private final String description;
    private boolean selected = false;

    public Option(String description) {
        this.description = description;
    }

    public String description(){
        return description;
    }

    public boolean isSelected() {
        return selected;
    }

    public void select() {
        selected = true;
    }
    public void unselect() {
        selected = false;
    }
}
