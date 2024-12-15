package Devices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Printer implements Runnable {

    private volatile boolean isRunning;

    private final DeviceSimulatorCLI deviceSimulatorCLI;

    private boolean isTampered;
    private boolean hasFailure;
    private final File outputFile;
    private FileWriter fileWriter;

    /**
     * Constructor for the Printer class.
     * 
     * @param fileName           the name of the file
     * @param deviceSimulatorCLI the device simulator CLI
     */
    public Printer(String fileName, DeviceSimulatorCLI deviceSimulatorCLI) {
        this.deviceSimulatorCLI = deviceSimulatorCLI;
        this.isTampered = false;
        this.hasFailure = false;
        this.outputFile = new File(fileName);
        this.isRunning = false;
    }

    /**
     * Check if the printer has paper.
     * 
     * @return true if the printer has paper, false otherwise
     */
    public boolean hasPaper() {
        return this.deviceSimulatorCLI.isPaperPlaced();
    }

    /**
     * Run the printer thread.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                // Main Loop Start
                if (this.deviceSimulatorCLI.isPrinterTampered()) {
                    this.isTampered = true;
                }
                if (this.deviceSimulatorCLI.doesPrinterHaveFailure()) {
                    this.hasFailure = true;
                }
                // Main Loop End
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("TamperSensor interrupted.");
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }

    /**
     * Turn on the printer (start thread).
     */
    public void turnOn() {
        this.isRunning = true;
        if (outputFile.exists()) {
            outputFile.delete();
        }
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Turn off the printer (stop thread).
     */
    public void turnOff() {
        this.closePrinterAccess();
        isRunning = false;
    }

    /**
     * Open the printer access (starts in append mode).
     */
    public void openPrinterAccess() {
        try {
            try {
                // Create a new empty file or clear the file if it already exists
                if (!outputFile.exists()) {
                    throw new IOException();
                }
            } catch (IOException e) {
                // toggleHasFailure();
                System.out.println("Error creating the file: " + e.getMessage());
            }
            fileWriter = new FileWriter(outputFile, true); // apend mode
            System.out.println("File opened for writing: " + outputFile.getName()); // DELETE
        } catch (IOException e) {
            System.out.println("Error opening the file: " + e.getMessage());
            hasFailure = true;
        }
    }

    /**
     * Close the printer access.
     */
    public void closePrinterAccess() {
        try {
            if (fileWriter != null) {
                fileWriter.flush(); // Ensure all data is written to the file
                fileWriter.close();
                System.out.println("File Closed: " + outputFile.getName());
            }
        } catch (IOException e) {
            System.out.println("Error closing the file: " + e.getMessage());
            hasFailure = true;
        }
    }

    /**
     * Prints a line (string) to the file attached to the printer
     * 
     * @param line the string we are printing
     */
    public void printLine(String line) {
        System.out.println("Printing a line");
        if (!hasFailure) {
            try {
                fileWriter.write(line + "\n");
                fileWriter.flush(); // Ensure the content is written to the file
            } catch (IOException e) {
                // toggleHasFailure();
                System.out.println("Error writing to the file: " + e.getMessage());
                hasFailure = true;
            }
        }
    }

    /**
     * Check if the printer is tampered.
     * 
     * @return true if the printer is tampered, false otherwise
     */
    public boolean isTampered() {
        return isTampered;
    }

    /**
     * Check if the printer has failure.
     * 
     * @return true if the printer has failure, false otherwise
     */
    public boolean hasFailure() {
        return hasFailure;
    }
}
