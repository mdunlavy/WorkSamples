package Software.Classes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Conversion {

    private static String electionName;
    private static LocalDate startDate;
    private static LocalDate endDate;
    private static LocalTime startForDay;
    private static LocalTime endForDay;

    public static ArrayList<Template> convertToTemplate(Ballot ballot) {
        ArrayList<Template> templates = new ArrayList<>();

        //store info
        electionName = ballot.electionName();
        startDate = ballot.startDate();
        endDate = ballot.endDate();
        startForDay = ballot.startForDay();
        endForDay = ballot.endForDay();

        for (Proposition proposition : ballot.propositions()) {
            Template template = new Template(
                    proposition.propName(),
                    proposition.propDesc(),
                    proposition.selectableOptions(),
                    proposition.options()
            );
            templates.add(template);
        }
        return templates;
    }

    public static String convertTemplateArrayListToString(ArrayList<Template> tem) {
        String st = "";
        int i = 0;
        for(Template t : tem){
            st += "t"+i + "\t";
            st += t.getMajorHeading() + "\t";
            st += t.getDescriptiveHeading() + "\t";
            st += t.getSelectionCount() + "\t";
            ArrayList<Option> arr = t.getOptions();
            for(Option option : arr) {
                st += option.getOptionAsString() + "\t";
            }
            i++;
        }
        return st;
    }

    public static ArrayList<Template> convertTemplateStringToTemplate(String s) {
        if (s == null || s.trim().isEmpty()) {
            System.out.println("[DEBUG] Input string is null or empty.");
            return new ArrayList<>(); // Return an empty list for null or empty input
        }
    
        ArrayList<Template> templates = new ArrayList<>();
        String[] sections = s.split("t\\d+\\t");
    
        for (String section : sections) {
            if (section.trim().isEmpty()) {
                continue; // Skip empty sections
            }
    
            String[] parts = section.split("\\t");
            if (parts.length < 3) {
                System.out.println("[DEBUG] Malformed section skipped: " + section);
                continue; // Skip sections that don't have at least three fields
            }
    
            String majorHeading = parts[0].trim();
            String descriptiveHeading = parts[1].trim();
            int selectionCount;
    
            try {
                selectionCount = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException e) {
                System.out.println("[DEBUG] Invalid selection count. Defaulting to 0.");
                selectionCount = 0;
            }
    
            ArrayList<Option> options = new ArrayList<>();
            for (int i = 3; i + 1 < parts.length; i += 2) {
                String description = parts[i].trim();
                boolean isSelected;
    
                try {
                    isSelected = Boolean.parseBoolean(parts[i + 1].trim());
                } catch (Exception e) {
                    System.out.println("[DEBUG] Invalid selection state for option: " + description);
                    isSelected = false; // Default to not selected
                }

                Option option = new Option(description);
                option.setSelected(isSelected);
                options.add(option);

            }
    
            templates.add(new Template(majorHeading, descriptiveHeading, selectionCount, options));
        }
    
        return templates;
    }
    
    
    public static Ballot convertToBallot(ArrayList<Template> templates) {
        ArrayList<Proposition> propositions = new ArrayList<>();
        for (Template template : templates) {
            Proposition proposition = new Proposition(
                    template.getMajorHeading(),
                    template.getDescriptiveHeading(),
                    template.getSelectionCount(),
                    template.getSelections()
            );
            propositions.add(proposition);
        }

        return new Ballot(electionName, startDate, endDate, startForDay, endForDay, propositions);
    }

    public static String[] votesInfoToString(Ballot ballot) {
        //determine the total number of selected options
        int selectedCount = 0;
        for (Proposition p : ballot.propositions()) {
            for (Option o : p.options()) {
                if (o.isSelected()) {
                    selectedCount++;
                }
            }
        }

        //create an array of appropriate size
        String[] votes = new String[selectedCount];
        int index = 0;

        for (Proposition p : ballot.propositions()) {
            for (Option o : p.options()) {
                if (o.isSelected()) {
                    System.out.println("Selected: " + o.description());
                    votes[index] = o.description();
                    index++;
                }
            }
        }

        return votes;
    }

}
