package Devices;

public class CardReader implements Runnable {

    private volatile boolean isRunning;

    private final DeviceSimulatorCLI deviceSimulatorCLI;

    private boolean isCardIn;
    private String cardCode;
    private boolean isTampered;
    private boolean hasFailure;

    /**
     * Constructor for the CardReader class.
     * @param deviceSimulatorCLI
     */
    public CardReader(DeviceSimulatorCLI deviceSimulatorCLI) {
        this.deviceSimulatorCLI = deviceSimulatorCLI;
        this.isCardIn = false;
        this.cardCode = null;
        this.isTampered = false;
        this.hasFailure = false;
    }

    /**
     * Turn on the card reader and start a new thread to simulate the card reader.
     */
    public void turnOn() {
        this.isRunning = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Turn off the card reader.
     */
    public void turnOff() {
        isRunning = false;
    }

    /**
     * Get the card code.
     * 
     * @return string of card code
     */
    public String getCardCode() {
        return cardCode;
    }

    /**
     * If the card is in the card reader, eject the card.
     */
    public void ejectCard() {
        if (isCardIn) {
            isCardIn = false;
            cardCode = null;
            this.deviceSimulatorCLI.ejectCard();
        }
    }

    /**
     * Check if the card reader is tampered.
     * 
     * @return true if the card reader is tampered, false otherwise
     */
    public boolean isTampered() {
        return isTampered;
    }

    /**
     * Check if the card reader has failure.
     * 
     * @return true if the card reader has failure, false otherwise
     */
    public boolean hasFailure() {
        return hasFailure;
    }

    /**
     * Run the card reader simulation.
     * This method is called when the thread is started.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                // Main Loop Start
                if (!this.isCardIn && this.deviceSimulatorCLI.isCardRecentlyInserted()) {
                    this.cardCode = this.deviceSimulatorCLI.getCardCode();
                }
                this.isCardIn = this.deviceSimulatorCLI.isCardInserted();
                if (this.deviceSimulatorCLI.isCardReaderTampered()) {
                    isTampered = true;
                }
                if (this.deviceSimulatorCLI.doesCardReaderHaveFailure()) {
                    hasFailure = true;
                }
                // Main Loop End
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("CardReader interrupted.");
            Thread.currentThread().interrupt();
        }
    }

}