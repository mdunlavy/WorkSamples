package Screen.screenControl;

import Ballot.Option;
import Screen.testSuite.Proposition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 *
 * TODO:
 * Redo/combine "navigate" functions to handle button inputs
 *                             "next, back continue" etc...
 *
 *
 * Setup "ShowProposition" to deal with single proposition
 *      Should take in a single proposition as a param instead of a list
 *
 *
 */

public class ScreenController extends Application {

    private Stage primaryStage;
    private Proposition proposition;
    private boolean isOn = false;

    private static ScreenController instance;

    private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);

    public ScreenController() {
        instance = this; // Set the static instance when constructed
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showScreen(new Ballot.Proposition("","",0,new ArrayList<Option>()), null);
    }


//    /**
//     * BUilds the sequential ballot
//     */
//    private void setupVotingProcess() {
//        voteScreens.clear();
//
//        Proposition welcomeProposition = new Proposition("Welcome", null, 0, new String[0]);
//        Proposition adminProposition = new Proposition("Admin", null, 0, new String[0]);
//
//        voteScreens.add(new VoteScreen(welcomeProposition, this));
//        voteScreens.add(new VoteScreen(adminProposition, this));
//        for (Proposition proposition : propositions) {
//            voteScreens.add(new VoteScreen(proposition, this));
//        }
//    }

    private void showScreen(Ballot.Proposition prop, String[] nav) {
        VoteScreen voteScreen = new VoteScreen(prop, this,nav);
        if (isOn) {
            Scene scene = voteScreen.createVotingScreen();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Voting System - Screen");
            primaryStage.show();
            if (prop.options().size() == 0){
                try {
                    queue.put(0);  // Put the value in the queue when button is pressed
                } catch (InterruptedException e) {
                    System.out.println("Error: Screen Controller buttonhandler blockingqueue is fucked up");
                    e.printStackTrace();
                }
            }
        } else if (!isOn) {
            Scene scene = voteScreen.drawOffScreen();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Voting System");
            primaryStage.show();
        }
    }


    /**
     * TODO: Handle navigation button presses!!!!
     *
     * buttonID is the index of the nav button in the proposition.
     */
    protected void buttonHandler(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonId = clickedButton.getId();
        try {
            queue.put(Integer.parseInt(buttonId));  // Put the value in the queue when button is pressed
        } catch (InterruptedException e) {
            System.out.println("Error: Screen Controller buttonhandler blockingqueue is fucked up");
            e.printStackTrace();
        }
    }

    // Method to block and wait for user selection
    public int waitForSelection() {
        try {
            return queue.take();  // Block until a button is pressed and a value is put in the queue
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;  // Return an error value if interrupted
        }
    }


    public static ScreenController getInstance() {
        return instance;
    }

    // Screen Controller API methods below

    /**
     * Method to turn the screen on, and start on the welcome screen
     */
    public void turnOn() {
        this.isOn = true;
        //setupVotingProcess();
        Platform.runLater(() -> instance.showScreen(new Ballot.Proposition("","",0,new ArrayList<Option>()),null));
    }


    /**
     * Method to turn the screen off, when screen is off, the submitted votes are not cleared from memory
     */
    public void turnOff() {
        this.isOn = false;
        //setupVotingProcess();
        Platform.runLater(() -> instance.showScreen(new Ballot.Proposition("","",0,new ArrayList<Option>()),null));
    }

    public void showProposition(Ballot.Proposition prop, String[] nav){

        Platform.runLater(() -> instance.showScreen(prop, nav));
    }

    /**
     * modify this to deal with a SINGULAR propisition
     *
     * i dont think this is needed - keegan
     * @param args
     */
//    /**
//     * This will set the propositions from which the user will be voted on
//     * @param propositions List<Proposition>
//     */
//    public void setPropositions(List<Proposition propositions) {
//        if (this.isOn) {
//            this.propositions = new ArrayList<>(propositions);
//            setupVotingProcess();
//            Platform.runLater(() -> instance.showScreen(0));
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}