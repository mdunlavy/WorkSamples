package Vis;

import Software.Classes.Conversion;
import Software.Classes.Option;
import Software.Classes.Template;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class TouchScreenGUI extends Application {
    private Stage primaryStage;

    private Socket serverSocket;
    private PrintWriter serverOutput;
    private BufferedReader serverInput;
    private final String host = "127.0.0.1";
    private final int port = 8080;
    private ArrayList<Template> templates = new ArrayList<>();
    private int currentTemplateIndex = 0;

    /**
     * Start the touch screen GUI.
     * 
     * @param primaryStage the primary stage
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        connectToScreen();
        startListeningToServer();

        primaryStage.setTitle("Voting System - Screen");
        primaryStage.setScene(drawOffScreen());
        primaryStage.show();
    }

    /**
     * Turn off the screen.
     * This method is called when the screen is turned off.
     */
    public void turnOff() {
        Platform.runLater(() -> {
            Stage stage = (Stage) primaryStage.getScene().getWindow();
            stage.setScene(drawOffScreen());
        });
        this.templates = new ArrayList<>();
    }

    /**
     * Main method to launch the application.
     * 
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void connectToScreen() {
        try {
            System.out.println("Attempting connection...");
            serverSocket = new Socket(InetAddress.getByName(host), port);
            serverInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            serverOutput = new PrintWriter(serverSocket.getOutputStream(), true);
            System.out.println("Connected to: " + serverSocket.getInetAddress().getHostName());
        } catch (IOException e) {
            System.out.println("Failed to connect to server: " + e.getMessage());
        }
    }

    private void startListeningToServer() {
        Thread serverListenerThread = new Thread(() -> {
            try {
                String serverResponse;
                while ((serverResponse = serverInput.readLine()) != null) {
                    String finalResponse = serverResponse;
                    Platform.runLater(() -> handleServerResponse(finalResponse));
                }
            } catch (IOException e) {
                System.out.println("Error reading server response: " + e.getMessage());
            }
        });
        serverListenerThread.setDaemon(true);
        serverListenerThread.start();
    }

    private void handleServerResponse(String serverResponse) {
        switch (serverResponse) {
            case "turn off" -> turnOff();
            case "next" -> navigateToNext();
            case "previous" -> navigateToPrevious();

            default -> handleTemplates(serverResponse);
        }
    }

    private void handleTemplates(String s) {
        // Convert the string from server back into templates
        this.templates = Conversion.convertTemplateStringToTemplate(s);
        this.currentTemplateIndex = 0;
        updateScreen();
    }

    private void passToServer(String message) {
        serverOutput.println(message);
        serverOutput.flush();
    }

    private Scene drawOffScreen() {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Label message = new Label("Screen is currently off");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        layout.getChildren().add(message);
        return new Scene(layout, 400, 600);
    }

    private Scene createTemplateScene(Template template) {
        BorderPane layout = new BorderPane(); // Main layout
        layout.setStyle("-fx-background-color: lightgray;");

        // Title and description at the top
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(10));
        Label majorHeading = new Label(template.getMajorHeading());
        majorHeading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        String descriptiveText = template.getDescriptiveHeading();
        descriptiveText = descriptiveText.replaceAll("(.{45})", "$1-\n-"); // Add a newline after every 15 characters
        Label descriptiveHeading = new Label(descriptiveText);
        descriptiveHeading.setStyle("-fx-font-size: 18px;");
        header.getChildren().addAll(majorHeading, descriptiveHeading);
        layout.setTop(header);

        // Center options area
        VBox optionsContainer = new VBox();
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setFillWidth(true);
        layout.setCenter(optionsContainer);

        ArrayList<Button> optionButtons = new ArrayList<>();
        for (Option option : template.getSelections()) {
            Button optionButton = new Button(option.description());
            optionButton.setStyle(option.isSelected()
                    ? "-fx-font-size: 16px; -fx-background-color: lightblue; -fx-border-color: gray; -fx-border-width: 1px;"
                    : "-fx-font-size: 16px; -fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1px;");
            optionButton.setMaxWidth(Double.MAX_VALUE); // Make buttons fill horizontally
            optionButton.setPrefHeight(600.0 / template.getSelections().size()); // Proportional height based on number
                                                                                 // of options

            // Toggle selection logic
            optionButton.setOnAction(e -> {
                int selectedCount = (int) template.getSelections().stream().filter(Option::isSelected).count();
                if (!option.isSelected() && selectedCount >= template.getSelectionCount()) {
                    return; // Prevent selecting more than allowed
                }
                option.setSelected(!option.isSelected());
                optionButton.setStyle(option.isSelected()
                        ? "-fx-font-size: 16px; -fx-background-color: lightblue; -fx-border-color: gray; -fx-border-width: 1px;"
                        : "-fx-font-size: 16px; -fx-background-color: white; -fx-border-color: gray; -fx-border-width: 1px;");
            });

            optionButtons.add(optionButton);
            optionsContainer.getChildren().add(optionButton);
        }

        // Navigation buttons at the bottom
        HBox buttonsBox = new HBox();
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(0); // No spacing
        buttonsBox.setStyle("-fx-padding: 0;"); // Remove padding/margins

        if (templates.size() > 1) {
            Button previousButton = new Button("Previous");
            previousButton.setDisable(currentTemplateIndex == 0);
            previousButton.setPrefSize(133, 50);
            previousButton.setOnAction(e -> passToServer("previous"));
            buttonsBox.getChildren().add(previousButton);
        }

        if (!template.getSelections().isEmpty()) {
            Button confirmButton = new Button("Confirm");
            confirmButton.setVisible(currentTemplateIndex == templates.size() - 1);
            confirmButton.setPrefSize(133, 50);
            confirmButton.setOnAction(e -> {
                String s = Conversion.convertTemplateArrayListToString(this.templates);
                passToServer(s);
            });
            buttonsBox.getChildren().add(confirmButton);
        }

        if (templates.size() > 1) {
            Button nextButton = new Button("Next");
            nextButton.setDisable(currentTemplateIndex == templates.size() - 1);
            nextButton.setPrefSize(133, 50);
            nextButton.setOnAction(e -> passToServer("next"));
            buttonsBox.getChildren().addAll(nextButton);
        }

        layout.setBottom(buttonsBox);

        return new Scene(layout, 400, 600);
    }

    private void navigateToNext() {
        if (currentTemplateIndex < templates.size() - 1) {
            currentTemplateIndex++;
            updateScreen();
        }
    }

    private void navigateToPrevious() {
        if (currentTemplateIndex > 0) {
            currentTemplateIndex--;
            updateScreen();
        }
    }

    private void updateScreen() {
        Platform.runLater(() -> {
            if (templates.isEmpty()) {
                System.out.println("[DEBUG] Templates list is empty. Showing default screen.");
                primaryStage.setScene(drawOffScreen());
                return;
            }

            Template currentTemplate = templates.get(currentTemplateIndex);
            Stage stage = (Stage) primaryStage.getScene().getWindow();
            stage.setScene(createTemplateScene(currentTemplate));
        });
    }

}
