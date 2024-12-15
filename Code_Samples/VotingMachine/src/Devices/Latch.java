package Devices;

public class Latch implements Runnable {
    private volatile boolean isRunning;

    private final DeviceSimulatorCLI deviceSimulatorCLI;

    private boolean isLocked;
    private boolean isTampered;
    private boolean hasFailure;

    /**
     * Constructor for the Latch class.
     * 
     * @param deviceSimulatorCLI the device simulator CLI
     */
    public Latch(DeviceSimulatorCLI deviceSimulatorCLI) {
        this.deviceSimulatorCLI = deviceSimulatorCLI;
        this.isLocked = false;
        this.isTampered = false;
        this.hasFailure = false;
        this.isRunning = false;
    }

    /**
     * Turn on the latch and start a new thread to simulate the latch.
     */
    public void turnOn() {
        this.isRunning = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    /**
     * Turn off running state.
     */
    public void turnOff() {
        isRunning = false;
    }

    /**
     * Check if the latch is locked.
     * 
     * @return true if the latch is locked, false otherwise
     */
    public boolean isLatchLocked() {
        return isLocked;
    }

    /**
     * Unlock the latch.
     */
    public void unlockLatch() {
        this.isLocked = false;
    }

    /**
     * Lock the latch.
     */
    public void lockLatch() {
        this.isLocked = true;
    }

    /**
     * Check if the latch is tampered.
     * 
     * @return true if the latch is tampered, false otherwise
     */
    public boolean isTampered() {
        return isTampered;
    }

    /**
     * Check if the latch has a failure.
     * 
     * @return true if the latch has a failure, false otherwise
     */
    public boolean hasFailure() {
        return hasFailure;
    }

    /**
     * Run the latch simulation.
     */
    @Override
    public void run() {
        try {
            while (isRunning) {
                // Main Loop Start
                if (this.deviceSimulatorCLI.isLatchTampered()) {
                    this.isTampered = true;
                }
                if (this.deviceSimulatorCLI.doesLatchHaveFailure()) {
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
}
