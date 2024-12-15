package Software;

import Devices.*;

public class VoteManager implements Runnable {
    private final DeviceMonitor monitor;
    private final User user;
    private final Admin admin;
    private final Voter voter;
    private final Thread UserThread;
    private final Thread MonitorThread;

    /**
     * Constructor for the VoteManager class.
     * 
     * @param printer          the printer
     * @param latch            the latch
     * @param cardReader       the card reader
     * @param blueSDCardDriver the blue SD card driver
     * @param redSDCardDriver01 the red SD card driver 01
     * @param redSDCardDriver02 the red SD card driver 02
     * @param tamperSensor     the tamper sensor
     * @param screenController the screen controller
     */
    public VoteManager(Printer printer, Latch latch, CardReader cardReader, SDCardDriver blueSDCardDriver,
            SDCardDriver redSDCardDriver01, SDCardDriver redSDCardDriver02, TamperSensor tamperSensor,
            ScreenController screenController) {
        this.monitor = new DeviceMonitor(printer, latch, cardReader, blueSDCardDriver, redSDCardDriver01,
                redSDCardDriver02, screenController, tamperSensor);
        MonitorThread = new Thread(this.monitor);
        MonitorThread.start();
        // Handles the Card Reader and verification that user code is valid for an Admin
        // or a Voter
        this.user = new User(cardReader);
        UserThread = new Thread(this.user);
        UserThread.start();
        // Used for the subprocesses of changing admin settings
        this.admin = new Admin(screenController, latch);
        // Used for the subprocess of voting for a ballot and saving/printing them
        this.voter = new Voter(printer, blueSDCardDriver, redSDCardDriver01, redSDCardDriver02, screenController);

    }

    /**
     * Run the vote manager thread.
     */
    @Override
    public void run() {
        try {
            // Wait for insertion of SD Cards and Printer Paper while checking if any
            // devices are tampered
            while (monitor.areSDCardsAndPaperNotLoaded() && monitor.isNotTamperedOrFailed()) {
                user.ejectCard();
                Thread.sleep(100);
            }
            // Continuously process user codes from the card reader
            while (true) {
                if (user.getUserType() == 'A') {
                    System.out.println("Admin Using Machine");
                    Thread adminManagerThread = new Thread(this.admin);
                    adminManagerThread.start();
                    while (adminManagerThread.isAlive()) {
                        if (!monitor.isNotTamperedOrFailed()) {
                            System.out.println("Tampering or failure detected! Interrupting thread...");
                            adminManagerThread.interrupt();
                            break;
                        }
                        Thread.sleep(500);
                    }
                } else if (user.getUserType() == 'V') {
                    if (this.admin.isLatchOpen() || !this.admin.isSessionOpen() || !this.admin.isElectionOpen()) {
                        System.out.println("LatchOpen/ElectionOrSessionNotStarted");
                    } else {
                        System.out.println("Voter Card Inserted");
                        Thread voterManagerThread = new Thread(this.voter);
                        voterManagerThread.start();
                        while (voterManagerThread.isAlive()) {
                            if (!monitor.isNotTamperedOrFailed()) {
                                System.out.println("Tampering or failure detected! Interrupting thread...");
                                voterManagerThread.interrupt();
                                break;
                            }
                            Thread.sleep(100);
                        }
                    }
                }
                user.ejectCard();
                // after processing user code check for tampering and failure
                if (!monitor.isNotTamperedOrFailed()) {
                    break;
                }
                Thread.sleep(1000);
                if (this.monitor.isShutDownReady() || this.admin.isShutOffReady()) {
                    this.monitor.turnOffDevices();
                    if (MonitorThread.isAlive()) {
                        MonitorThread.interrupt();
                    }
                    if (UserThread.isAlive()) {
                        UserThread.interrupt();
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("VoteManager interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}
