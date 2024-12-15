package Devices;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SDCardDriver implements Runnable {

    private File file;
    private final boolean modeIsRead;

    private volatile boolean isRunning;

    private final DeviceSimulatorCLI deviceSimulatorCLI;
    private final int SD_ID;

    private boolean isTampered;
    private boolean hasFailure;
    private boolean isInserted;

    private FileWriter fileWriter;
    private FileReader fileReader;
    private BufferedReader bufferedReader;

    /**
     * Constructor for the SDCardDriver class.
     * 
     * @param filePath           the file path
     * @param modeIsRead         boolean for read mode
     * @param deviceSimulatorCLI the device simulator CLI
     * @param SD_ID              the SD ID
     */
    public SDCardDriver(String filePath, boolean modeIsRead, DeviceSimulatorCLI deviceSimulatorCLI, int SD_ID) {
        this.deviceSimulatorCLI = deviceSimulatorCLI;
        this.SD_ID = SD_ID;
        this.hasFailure = false;
        this.isTampered = false;
        this.isInserted = false;
        this.file = new File(filePath);
        this.modeIsRead = modeIsRead;
        this.isRunning = false;
    }

    /**
     * Turn on the SD card driver and start a new thread to simulate the SD
     * card driver.
     */
    public void turnOn() {
        this.isRunning = true;
        if (!this.modeIsRead) {
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Turn off the SD card driver.
     */
    public void turnOff() {
        this.closeSDCardAccess();
        isRunning = false;
    }

    /**
     * Open the SD card access.
     */
    public void openSDCardAccess() {
        try {
            if (this.modeIsRead) {
                if (!file.exists()) {
                    throw new IOException();
                }
            } else {
                if (!file.exists()) {
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating the file: " + e.getMessage());
        }
        try {
            if (this.modeIsRead) {
                this.fileReader = new FileReader(file);
                this.bufferedReader = new BufferedReader(fileReader);
            } else {
                fileWriter = new FileWriter(file, true);
            }
        } catch (IOException e) {
            System.out.println("Error opening the file: " + e.getMessage());
            hasFailure = true;
        }
    }

    /**
     * Close the SD card access.
     */
    public void closeSDCardAccess() {
        try {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
                fileWriter = null;
                System.out.println("File closed for writing: " + file.getName());
            }
            if (bufferedReader != null) {
                bufferedReader.close();
                bufferedReader = null;
                System.out.println("BufferedReader closed for file: " + file.getName());
            }
            if (fileReader != null) {
                fileReader.close();
                fileReader = null;
                System.out.println("FileReader closed for file: " + file.getName());
            }
        } catch (IOException e) {
            System.out.println("Error closing the file: " + e.getMessage());
            hasFailure = true;
        }
    }

    /**
     * Read lines from the file.
     * 
     * @return a list of strings
     */
    public List<String> readLines() {
        List<String> lines = new ArrayList<>();
        if (!hasFailure && modeIsRead) {
            try {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading from the file: " + e.getMessage());
                this.hasFailure = true;
            }
        } else {
            System.out.println("Driver is not in read mode or has failed.");
        }
        return lines;
    }

    /**
     * Write a line to the file.
     * 
     * @param line the line to write
     */
    public void writeLine(String line) {
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
     * Check if the SD card driver is tampered.
     * 
     * @return true if the SD card driver is tampered, false otherwise
     */
    public boolean hasFailure() {
        return hasFailure;
    }

    /**
     * Check if the SD card driver is tampered.
     * 
     * @return true if the SD card driver is tampered, false otherwise
     */
    public boolean isTampered() {
        return isTampered;
    }

    /**
     * Check if the SD card driver is inserted.
     * 
     * @return true if the SD card driver is inserted, false otherwise
     */
    public boolean isInserted() {
        return isInserted;
    }

    /**
     * Run the SD card driver simulation.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                // Main Loop Start
                if (this.deviceSimulatorCLI.isSDCardDriverTampered(this.SD_ID)) {
                    this.isTampered = true;
                }
                if (this.deviceSimulatorCLI.doesSDCardDriverHaveFailure(this.SD_ID)) {
                    this.hasFailure = true;
                }
                if (this.deviceSimulatorCLI.isSDCardInserted(this.SD_ID)) {
                    this.isInserted = true;
                }
                // Main Loop End
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("TamperSensor interrupted.");
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }
    }
}
