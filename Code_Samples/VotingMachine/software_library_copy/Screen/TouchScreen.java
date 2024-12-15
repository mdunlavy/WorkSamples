package Screen;

import Software.Classes.Option;
import Software.Classes.Template;

public class TouchScreen {

    private Template template; // The currently displayed template
    private boolean isScreenOn = false;
    private boolean hasFailure = false;

    public TouchScreen() {

    }

    /**
     * Turn the screen on.
     */
    public void screenOn() {
        isScreenOn = true;
        System.out.println("Screen is ON.");
    }

    /**
     * Turn the screen off.
     */
    public void screenOff() {
        isScreenOn = false;
        System.out.println("Screen is OFF.");
    }

    /**
     * Present the given template on the screen.
     *
     * @param template The Template object to display on the screen.
     */
    public void presentTemplate(Template template) {
        if (isScreenOn) {
            this.template = template;
            System.out.println("Presenting template: " + template.getMajorHeading());
            System.out.println("Description: " + template.getDescriptiveHeading());

            // Display options
            for (Option option : template.getSelections()) {
                System.out.println("Option: " + option.description() +
                        (option.isSelected() ? " [Selected]" : ""));
            }
        } else {
            System.out.println("Screen is OFF. Cannot present template.");
        }
    }

    /**
     * Return the current template, including user modifications.
     *
     * @return The current Template object being displayed.
     */
    public Template returnTemplate() {
        if (isScreenOn && template != null) {
            return template;
        }
        System.out.println("No template to return or screen is OFF.");
        return null;
    }

    /**
     * Check if the user has completed their interaction with the current template.
     *
     * @return true if the user has made all required selections, false otherwise.
     */
    public boolean exitReady() {
        if (isScreenOn && template != null) {
            long selectedCount = template.getSelections().stream()
                    .filter(Option::isSelected)
                    .count();

            return selectedCount >= template.getSelectionCount();
        }

        return false; // Not ready if the screen is off or no template is set
    }

    /**
     * Check if the screen has failed.
     *
     * @return true if the screen has a failure, false otherwise.
     */
    public boolean hasFailure() {
        return hasFailure;
    }
}
