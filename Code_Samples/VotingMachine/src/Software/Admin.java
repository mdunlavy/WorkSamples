package Software;

import Devices.Latch;
import Devices.ScreenController;
import Software.Classes.Option;
import Software.Classes.Template;

import java.util.ArrayList;

public class Admin implements Runnable {

    private final ScreenController screenController;
    private final Latch latch;
    private Template adminTemplate;
    private boolean ElectionOpen;
    private boolean SessionOpen;
    private boolean ShutOffFlag;

    /**
     * Constructor for the Admin class.
     * 
     * @param screenController the screen controller
     * @param latch            the latch
     */
    public Admin(ScreenController screenController, Latch latch) {
        this.screenController = screenController;
        this.latch = latch;
        this.ElectionOpen = false;
        this.SessionOpen = false;
        this.ShutOffFlag = false;
        initializeAdminTemplate();
    }

    /**
     * Check if the latch is open.
     * 
     * @return true if the latch is open, false otherwise
     */
    public boolean isLatchOpen() {
        return !this.latch.isLatchLocked();
    }

    /**
     * Check if the election is open.
     * 
     * @return true if the election is open, false otherwise
     */
    public boolean isElectionOpen() {
        return this.ElectionOpen;
    }

    /**
     * Check if the session is open.
     * 
     * @return true if the session is open, false otherwise
     */
    public boolean isSessionOpen() {
        return SessionOpen;
    }

    public boolean isShutOffReady() {
        return ShutOffFlag;
    }

    /**
     * Runs the admin workflow: displays the template and processes selections.
     */
    public void run() {
        System.out.println("Admin Using Machine");
        ArrayList<Template> templates = new ArrayList<>();
        templates.add(adminTemplate);
        screenController.showScreen(templates);
        while (true) {
            if (screenController.isFinished()) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ArrayList<Template> adminTemplatesAfter = this.screenController.returnTemplates();
        this.adminTemplate = adminTemplatesAfter.getFirst();
        handleSelections(this.adminTemplate);
        if (this.ElectionOpen && this.SessionOpen && this.latch.isLatchLocked()) {
            Template welcomeScreen = new Template("Welcome", "Welcome to the Voting System!", 1,
                    new ArrayList<>());
            ArrayList<Template> screens = new ArrayList<>();
            screens.add(welcomeScreen);
            screenController.showScreen(screens);
        }
    }

    /**
     * Initialize the admin template with options for managing the election.
     */
    private void initializeAdminTemplate() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("Open Election"));
        options.add(new Option("Open Session"));
        options.add(new Option("Lock Latches"));
        options.add(new Option("Shut Off"));

        adminTemplate = new Template(
                "Admin Panel",
                "Select an action to manage the election:",
                4, // Allow only one selection at a time
                options);
    }

    private void handleSelections(Template tem) {
        for (int i = 0; i < tem.getOptions().size(); i++) {
            switch (tem.getOptions().get(i).description()) {
                case "Open Election":
                    this.ElectionOpen = tem.getOptions().get(i).isSelected();
                    break;
                case "Open Session":
                    this.SessionOpen = tem.getOptions().get(i).isSelected();
                    break;
                case "Lock Latches":
                    if (tem.getOptions().get(i).isSelected()) {
                        this.latch.lockLatch();
                    } else {
                        this.latch.unlockLatch();
                    }
                    break;
                case "Shut Off":
                    this.ShutOffFlag = tem.getOptions().get(i).isSelected();
                    break;
            }
        }
    }
}