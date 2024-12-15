package Software;

import Devices.*;
import Software.Classes.Option;
import Software.Classes.Template;

import java.util.ArrayList;

/**
 * This class is a subcomponent of the Voting Machine internal software
 * and handles monitoring the devices to see if they are failing, have been
 * tampered, and
 * have sd cards or printer paper inserted for initialization. Also handles
 * opening access to these
 * devices/files and turning on and off the devices.
 */
public class DeviceMonitor implements Runnable {
    private final Printer printer;
    private final Latch latch;
    private final CardReader cardReader;
    private final SDCardDriver blueSDCardDriver;
    private final SDCardDriver redSDCardDriver01;
    private final SDCardDriver redSDCardDriver02;
    private final ScreenController screenController;
    private final TamperSensor tamperSensor;
    private boolean isTamperedOrFailed;
    private boolean shut_off_flag;

    /**
     * Constructor for the DeviceMonitor class.
     * 
     * @param printer           the printer
     * @param latch             the latch
     * @param cardReader        the card reader
     * @param blueSDCardDriver  the blue SD card driver
     * @param redSDCardDriver01 the red SD card driver 01
     * @param redSDCardDriver02 the red SD card driver 02
     * @param screenController  the screen controller
     * @param tamperSensor      the tamper sensor
     */
    public DeviceMonitor(Printer printer, Latch latch, CardReader cardReader, SDCardDriver blueSDCardDriver,
            SDCardDriver redSDCardDriver01, SDCardDriver redSDCardDriver02, ScreenController screenController,
            TamperSensor tamperSensor) {
        this.printer = printer;
        this.latch = latch;
        this.cardReader = cardReader;
        this.blueSDCardDriver = blueSDCardDriver;
        this.redSDCardDriver01 = redSDCardDriver01;
        this.redSDCardDriver02 = redSDCardDriver02;
        this.screenController = screenController;
        this.tamperSensor = tamperSensor;
        this.isTamperedOrFailed = false;
        this.shut_off_flag = false;
    }

    /**
     * Check if there are any tampers or failures in the devices
     * 
     * @return boolean
     */
    public boolean isNotTamperedOrFailed() {
        return !this.isTamperedOrFailed;
    }

    /**
     * Check if the sd cards and paper are not inserted into the machine
     * 
     * @return boolean
     */
    public boolean areSDCardsAndPaperNotLoaded() {
        return !(this.blueSDCardDriver.isInserted() && this.redSDCardDriver01.isInserted()
                && this.redSDCardDriver02.isInserted() && this.printer.hasPaper());
    }

    public boolean isShutDownReady() {
        return this.shut_off_flag;
    }

    public void turnOffDevices() {
        this.blueSDCardDriver.turnOff();
        this.redSDCardDriver01.turnOff();
        this.redSDCardDriver02.turnOff();
        this.printer.turnOff();
        this.latch.turnOff();
        this.cardReader.turnOff();
        this.screenController.turnOff();
        this.tamperSensor.turnOff();
    }

    /**
     * Run the device monitor thread.
     */
    @Override
    public void run() {
        try {
            turnOnDevices();
            while (!this.checkForFailuresAndTampers()) {
                Thread.sleep(100);
            }
            this.isTamperedOrFailed = true;
            ArrayList<Template> templates = new ArrayList<>();
            Template template = createFailureTamperTemplate();
            templates.add(template);
            screenController.showScreen(templates);
            while (true) {
                if (screenController.isFinished() && handleSelections(screenController.returnTemplates().getFirst())
                        && cardReader.getCardCode() != null && cardReader.getCardCode().charAt(0) == 'A') {
                    break;
                }
                if (screenController.isFinished()) {
                    screenController.showScreen(templates);
                }
                if (cardReader.getCardCode() == null || cardReader.getCardCode().charAt(0) != 'A') {
                    this.cardReader.ejectCard();
                }
                System.out.println("Not Finished");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            turnOffDevices();
            this.shut_off_flag = true;
        } catch (InterruptedException e) {
            System.out.println("Device Monitor interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    private boolean handleSelections(Template tem) {
        for (int i = 0; i < tem.getOptions().size(); i++) {
            switch (tem.getOptions().get(i).description()) {
                case "SHUT DOWN":
                    return tem.getOptions().get(i).isSelected();
            }
        }
        return false;
    }

    private Template createFailureTamperTemplate() {
        ArrayList<Option> options = new ArrayList<>();
        options.add(new Option("SHUT DOWN"));

        return new Template(
                "Failure or Tampering Occurred",
                "Is it okay to shut down the machine (insert admin card)",
                1,
                options);
    }

    private boolean checkForFailuresAndTampers() {
        return this.tamperSensor.isTampered() || printer.hasFailure() || this.latch.hasFailure()
                || this.cardReader.hasFailure()
                || this.blueSDCardDriver.hasFailure() || this.redSDCardDriver01.hasFailure()
                || this.redSDCardDriver02.hasFailure() || this.screenController.hasFailure();
    }

    private void turnOnDevices() {
        this.blueSDCardDriver.turnOn();
        this.redSDCardDriver01.turnOn();
        this.redSDCardDriver02.turnOn();
        this.printer.turnOn();
        this.latch.turnOn();
        this.cardReader.turnOn();
        this.screenController.turnOn();
        this.tamperSensor.turnOn();
    }
}
