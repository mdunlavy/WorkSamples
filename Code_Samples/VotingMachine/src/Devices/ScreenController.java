package Devices;

import Vis.TouchScreenGUI;
import javafx.application.Application;
import Software.Classes.*;
import java.util.ArrayList;

import java.io.*;
import java.net.*;

public class ScreenController implements Runnable { // Implements Runnable
    private ArrayList<Template> templates = new ArrayList<>();
    private int currentTemplateIndex = 0;

    private final DeviceSimulatorCLI deviceSimulatorCLI;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter clientOutput;
    private BufferedReader clientInput;
    private final int port = 8080;
    private boolean isTampered;
    private boolean hasFailure;
    private boolean isFinished;
    private volatile boolean isRunning;
    private Thread serverThread;
    private Thread clientThread;
    private Thread monitoringThread;

    /**
     * Constructor for the ScreenController class.
     * 
     * @param deviceSimulatorCLI
     */
    public ScreenController(DeviceSimulatorCLI deviceSimulatorCLI) {
        this.deviceSimulatorCLI = deviceSimulatorCLI;

    }

    /**
     * Turn on the screen controller and start a new thread to simulate the screen
     * controller.
     */
    public void turnOn() {
        isRunning = true;
        serverThread = new Thread(this);
        serverThread.start();

        clientThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                Application.launch(TouchScreenGUI.class);
            } catch (InterruptedException e) {
                System.out.println("Client thread interrupted.");
            }
        });
        clientThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Turn-on delay interrupted.");
        }

        // Start the monitoring thread
        monitoringThread = new Thread(this::monitorStates);
        monitoringThread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Turn-on delay interrupted.");
        }
    }

    /**
     * Turn off the screen controller.
     */
    public void turnOff() {
        isRunning = false;
        try {
            javafx.application.Platform.runLater(() -> {
                try {
                    javafx.application.Platform.exit();
                } catch (RuntimeException e) {
                    System.out.println("RuntimeException while exiting JavaFX: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.out.println("Error shutting down JavaFX application: " + e.getMessage());
        }

        // Stop monitoring thread gracefully
        if (monitoringThread != null && monitoringThread.isAlive()) {
            monitoringThread.interrupt();
        }
    }

    /**
     * Show the screen with the given templates.
     * 
     * @param tem the templates to show
     */
    public void showScreen(ArrayList<Template> tem) {
        this.isFinished = false;
        this.templates = tem;
        String s = Conversion.convertTemplateArrayListToString(tem);
        passToClient(s);
    }

    /**
     * Check if the screen controller is finished.
     * 
     * @return true if the screen controller is finished, false otherwise
     */
    public boolean isFinished() {
        return this.isFinished;
    }

    /**
     * Return the templates selected by the user.
     * 
     * @return the selected templates
     */
    public ArrayList<Template> returnTemplates() {
        for (int i = 0; i < templates.getFirst().getOptions().size(); i++) {
            switch (templates.getFirst().getOptions().get(i).description()) {
                case "Open Election":
                    System.out.println(templates.getFirst().getOptions().get(i).isSelected());
                    break;
                case "Open Session":
                    System.out.println(templates.getFirst().getOptions().get(i).isSelected());
                    break;
                case "Open Latches":
                    System.out.println(templates.getFirst().getOptions().get(i).isSelected());
                    break;
            }
        }
        ArrayList<Template> temp = this.templates;
        this.templates = new ArrayList<>();
        return temp;
    }

    /**
     * Check if the screen controller has failure.
     * 
     * @return true if the screen controller has failure, false otherwise
     */
    public boolean hasFailure() {
        return this.hasFailure;
    }

    /**
     * Check if the screen controller is tampered.
     * 
     * @return true if the screen controller is tampered, false otherwise
     */
    public boolean isTampered() {
        return this.isTampered;
    }

    /**
     * Run the screen controller simulation.
     */
    @Override
    public void run() {
        startServer();
    }

    private void navigateToNextTemplate() {
        if (!templates.isEmpty() && currentTemplateIndex < templates.size() - 1) {
            currentTemplateIndex++;
        } else {
            System.out.println("Already at the last template or no templates available.");
        }
    }

    private void navigateToPreviousTemplate() {
        if (!templates.isEmpty() && currentTemplateIndex > 0) {
            currentTemplateIndex--;
        } else {
            System.out.println("Already at the first template or no templates available.");
        }
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            connectClient();
            listenForClientInput();
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private void connectClient() throws IOException {
        System.out.println("Waiting for client connection...");
        clientSocket = serverSocket.accept();
        clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        clientOutput = new PrintWriter(clientSocket.getOutputStream(), true);

        System.out.println("Connected to: " + clientSocket.getInetAddress().getHostName());
    }

    private void listenForClientInput() throws IOException {
        String input;
        while ((input = clientInput.readLine()) != null && isRunning) {
            System.out.println("Received: " + input);
            handleClientCommand(input);
//            if (this.deviceSimulatorCLI.doesScreenHaveFailure()) {
//                this.hasFailure = true;
//            }
//            if (this.deviceSimulatorCLI.isScreenTampered()) {
//                this.isTampered = true;
//            }
        }
        System.out.println("Client disconnected.");
        clientSocket.close();
    }

    private void passToClient(String message) {
        System.out.println("Sending to client: " + message);
        clientOutput.println(message);
        clientOutput.flush();
    }

    private void handleClientCommand(String command) throws IOException {
        switch (command) {
            case "next" -> {
                navigateToNextTemplate();
                passToClient("next");
            }
            case "previous" -> {
                navigateToPreviousTemplate();
                passToClient("previous");
            }
            default -> { // confirm
                System.out.println("Client confirmed selection.");
                this.isFinished = true;
                this.templates = Conversion.convertTemplateStringToTemplate(command);
                passToClient("turn off");
            }
        }
    }

    /**
     * Background task to monitor tampering and failure states.
     */
    private void monitorStates() {
        while (isRunning) {
            try {
                // Check for tampering and failure
                if (this.deviceSimulatorCLI.doesScreenHaveFailure()) {
                    this.hasFailure = true;
                }

                if (this.deviceSimulatorCLI.isScreenTampered()) {
                    this.isTampered = true;
                    System.out.println("Tampering detected.");
                }

                // Pause for a short period to avoid high CPU usage
                Thread.sleep(500); // Adjust the interval as needed
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
