package Software.Classes;

import java.util.ArrayList;

public class Template {
    private String majorHeading; //major title of the screen
    private String descriptiveHeading; //detailed description
    private int selectionCount; //number of selectable options (0-5)
    private ArrayList<Option> options;

    //constructor
    public Template(String majorHeading, String descriptiveHeading, int selectionCount, ArrayList<Option> options) {
        this.majorHeading = majorHeading;
        this.descriptiveHeading = descriptiveHeading;
        this.selectionCount = selectionCount;
        this.options = options;
    }
    public ArrayList<Option> getOptions(){
        return options;
    }

    public String getMajorHeading() {
        return majorHeading;
    }

    public String getDescriptiveHeading() {
        return descriptiveHeading;
    }

    public int getSelectionCount() {
        return selectionCount;
    }

    public ArrayList<Option> getSelections() {
        return options;
    }
}