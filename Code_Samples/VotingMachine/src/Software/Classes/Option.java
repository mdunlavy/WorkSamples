package Software.Classes;
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

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getOptionAsString(){
        String s = description + "\t" + selected;
        return s;
    }
}
