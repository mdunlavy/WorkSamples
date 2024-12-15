package Devices;

public class TamperSensor implements Runnable {
    // Devices which the TamperSensor checks
    private final Printer printer;
    private final Latch latch;
    private final CardReader cardReader;
    private final SDCardDriver blueSDCardDriver;
    private final SDCardDriver redSDCardDriver01;
    private final SDCardDriver redSDCardDriver02;
    private final ScreenController screenController;

    private volatile boolean isRunning;
    private volatile boolean tampered;

    /**
     * Constructor for the TamperSensor class.
     * 
     * @param printer           the printer
     * @param latch             the latch
     * @param cardReader        the card reader
     * @param blueSDCardDriver  the blue SD card driver
     * @param redSDCardDriver01 the red SD card driver 01
     * @param redSDCardDriver02 the red SD card driver 02
     * @param screenController  the screen controller
     */
    public TamperSensor(Printer printer, Latch latch, CardReader cardReader, SDCardDriver blueSDCardDriver,
            SDCardDriver redSDCardDriver01, SDCardDriver redSDCardDriver02, ScreenController screenController) {
        this.printer = printer;
        this.latch = latch;
        this.cardReader = cardReader;
        this.blueSDCardDriver = blueSDCardDriver;
        this.redSDCardDriver01 = redSDCardDriver01;
        this.redSDCardDriver02 = redSDCardDriver02;
        this.screenController = screenController;
        this.tampered = false;
        this.isRunning = false;
    }

    /**
     * Turn on the tamper sensor and start a new thread to simulate the tamper
     * sensor.
     */
    public void turnOn() {
        this.isRunning = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Turn off the tamper sensor.
     */
    public void turnOff() {
        isRunning = false;
    }

    /**
     * Check if the tamper sensor is tampered.
     * 
     * @return true if the tamper sensor is tampered, false otherwise
     */
    public boolean isTampered() {
        return tampered;
    }

    /**
     * Run thread for the tamper sensor.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                if (printer.isTampered() || latch.isTampered() || this.cardReader.isTampered()
                        || this.blueSDCardDriver.isTampered() || this.redSDCardDriver01.isTampered()
                        || this.redSDCardDriver02.isTampered() || this.screenController.isTampered()) {
                    this.tampered = true;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("TamperSensor interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}
