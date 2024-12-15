package Devices;

import java.util.Scanner;

public class DeviceSimulatorCLI implements Runnable {
    // Printer
    private volatile boolean printerTampered;
    private volatile boolean printerFailed;
    private volatile boolean paperPlaced;
    // Latch
    private volatile boolean latchTampered;
    private volatile boolean latchFailed;
    // Card Reader
    private volatile boolean cardReaderTampered;
    private volatile boolean cardReaderFailed;
    private volatile boolean isCardInserted;
    private volatile boolean isCardRecentlyInserted;
    private volatile String cardCode;
    // SD Card Drivers
    private volatile boolean blueSDCardDriverTampered;
    private volatile boolean blueSDCardDriverFailed;
    private volatile boolean redSDCardDriverTampered01;
    private volatile boolean redSDCardDriverFailed01;
    private volatile boolean redSDCardDriverTampered02;
    private volatile boolean redSDCardDriverFailed02;
    private volatile boolean blueSDCardInserted;
    private volatile boolean redSDCardInserted01;
    private volatile boolean redSDCardInserted02;
    private volatile boolean screenFailed;
    private volatile boolean screenTampered;

    /**
     * Constructor for the DeviceSimulatorCLI class initializes values.
     */
    public DeviceSimulatorCLI() {
        this.printerTampered = false;
        this.printerFailed = false;
        this.latchTampered = false;
        this.latchFailed = false;
        this.cardReaderTampered = false;
        this.cardReaderFailed = false;
        this.isCardInserted = false;
        this.isCardRecentlyInserted = false;
        this.cardCode = null;
        this.blueSDCardDriverTampered = false;
        this.blueSDCardDriverFailed = false;
        this.redSDCardDriverTampered01 = false;
        this.redSDCardDriverFailed01 = false;
        this.redSDCardDriverTampered02 = false;
        this.redSDCardDriverFailed02 = false;
        this.blueSDCardInserted = false;
        this.redSDCardInserted01 = false;
        this.redSDCardInserted02 = false;
        this.paperPlaced = false;
        this.screenFailed = false;
        this.screenTampered = false;
    }

    /**
     * Get the tampered status of the printer.
     * 
     * @return true if the printer is tampered, false otherwise
     */
    public boolean isPrinterTampered() {
        return printerTampered;
    }

    /**
     * Get the failure status of the printer.
     * 
     * @return true if the printer has failed, false otherwise
     */
    public boolean doesPrinterHaveFailure() {
        return printerFailed;
    }

    /**
     * Get the tampered status of the latch.
     * 
     * @return true if the latch is tampered, false otherwise
     */
    public boolean isLatchTampered() {
        return latchTampered;
    }

    /**
     * Get the failure status of the latch.
     * 
     * @return true if the latch has failed, false otherwise
     */
    public boolean doesLatchHaveFailure() {
        return latchFailed;
    }

    /**
     * Get the tampered status of the card reader.
     * 
     * @return true if the card reader is tampered, false otherwise
     */
    public boolean isCardReaderTampered() {
        return cardReaderTampered;
    }

    /**
     * Get the failure status of the card reader.
     * 
     * @return true if the card reader has failed, false otherwise
     */
    public boolean doesCardReaderHaveFailure() {
        return cardReaderFailed;
    }

    /**
     * Get the status of the card reader.
     * 
     * @return true if a card is inserted, false otherwise
     */
    public boolean isCardInserted() {
        return isCardInserted;
    }

    /**
     * Get the status of the card reader.
     * 
     * @return true if a card was recently inserted, false otherwise
     */
    public boolean isCardRecentlyInserted() {
        boolean temp = isCardRecentlyInserted;
        isCardRecentlyInserted = false;
        return temp;
    }

    /**
     * Get the card code.
     * 
     * @return string of card code
     */
    public String getCardCode() {
        return this.cardCode;
    }

    /**
     * Get the tampered status of the SD Card Driver.
     * 
     * @param SD_ID The ID of the SD Card Driver
     * @return true if the SD Card Driver is tampered, false otherwise
     */
    public boolean isSDCardDriverTampered(int SD_ID) {
        switch (SD_ID) {
            case 1 -> {
                return blueSDCardDriverTampered;
            }
            case 2 -> {
                return redSDCardDriverTampered01;
            }
            case 3 -> {
                return redSDCardDriverTampered02;
            }
            default -> {
                return true;
            }
        }
    }

    /**
     * Get the failure status of the SD Card Driver.
     * 
     * @param SD_ID The ID of the SD Card Driver
     * @return true if the SD Card Driver has failed, false otherwise
     */
    public boolean doesSDCardDriverHaveFailure(int SD_ID) {
        switch (SD_ID) {
            case 1 -> {
                return blueSDCardDriverFailed;
            }
            case 2 -> {
                return redSDCardDriverFailed01;
            }
            case 3 -> {
                return redSDCardDriverFailed02;
            }
            default -> {
                return true;
            }
        }
    }

    /**
     * Get the status of the SD Card.
     * 
     * @param SD_ID The ID of the SD Card
     * @return true if the SD Card is inserted, false otherwise
     */
    public boolean isSDCardInserted(int SD_ID) {
        switch (SD_ID) {
            case 1 -> {
                return blueSDCardInserted;
            }
            case 2 -> {
                return redSDCardInserted01;
            }
            case 3 -> {
                return redSDCardInserted02;
            }
            default -> {
                return true;
            }
        }
    }

    /**
     * If the card is in the card reader, eject the card.
     */
    public void ejectCard() {
        this.isCardRecentlyInserted = false;
        this.isCardInserted = false;
    }

    /**
     * Get the status of the printer paper.
     * 
     * @return true if the printer paper is placed, false otherwise
     */
    public boolean isPaperPlaced() {
        return this.paperPlaced;
    }

    /**
     * Get the status of the touch screen.
     * 
     * @return true if the touch screen has failed, false otherwise
     */
    public boolean doesScreenHaveFailure() {
        return this.screenFailed;
    }

    /**
     * Get the tampered status of the touch screen.
     * 
     * @return true if the touch screen is tampered, false otherwise
     */
    public boolean isScreenTampered() {
        return this.screenTampered;
    }

    /**
     * Run the Device Simulator CLI.
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Device Simulator CLI started. Type 'help' for commands.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim(); // Read and trim input
            handleCommand(input);
        }
    }

    /**
     * Handles the commands entered by the user.
     *
     * @param command The command string entered by the user.
     */
    private void handleCommand(String command) {
        switch (command.toLowerCase()) {

            case "t printer":
                this.printerTampered = true;
                System.out.println("Printer tampered status set to TRUE.");
                break;

            case "f printer":
                this.printerFailed = true;
                System.out.println("Printer failure status set to TRUE.");
                break;

            case "t latch":
                this.latchTampered = true;
                System.out.println("Latch tampered status set to TRUE.");
                break;

            case "f latch":
                this.latchFailed = true;
                System.out.println("Latch failure status set to TRUE.");
                break;

            case "t cardreader":
                this.cardReaderTampered = true;
                System.out.println("CardReader tampered status set to TRUE.");
                break;

            case "f cardreader":
                this.cardReaderFailed = true;
                System.out.println("CardReader failure status set to TRUE.");
                break;

            case "i admin":
                this.isCardRecentlyInserted = true;
                this.isCardInserted = true;
                this.cardCode = "A123";
                System.out.println("Admin card being inserted");
                break;

            case "i voter":
                this.isCardRecentlyInserted = true;
                this.isCardInserted = true;
                this.cardCode = "V456";
                System.out.println("Voter card being inserted");
                break;

            case "i other":
                this.isCardRecentlyInserted = true;
                this.isCardInserted = true;
                this.cardCode = "DOGE";
                System.out.println("Other card being inserted");
                break;

            case "t bluedriver":
                this.blueSDCardDriverTampered = true;
                System.out.println("Blue SDCardDriver tampered status set to TRUE.");
                break;

            case "f bluedriver":
                this.blueSDCardDriverFailed = true;
                System.out.println("Blue SDCardDriver failure status set to TRUE.");
                break;

            case "t reddriver1":
                this.redSDCardDriverTampered01 = true;
                System.out.println("Red SDCardDriver 01 tampered status set to TRUE.");
                break;

            case "f reddriver1":
                this.redSDCardDriverFailed01 = true;
                System.out.println("Red SDCardDriver 01 failure status set to TRUE.");
                break;

            case "t reddriver2":
                this.redSDCardDriverTampered02 = true;
                System.out.println("Red SDCardDriver 02 tampered status set to TRUE.");
                break;

            case "f reddriver2":
                this.redSDCardDriverFailed02 = true;
                System.out.println("Red SDCardDriver 02 failure status set to TRUE.");
                break;

            case "p bluesd":
                this.blueSDCardInserted = true;
                System.out.println("Blue SD Card placed into slot");
                break;

            case "p redsd1":
                this.redSDCardInserted01 = true;
                System.out.println("Red SD Card 1 placed into slot");
                break;

            case "p redsd2":
                this.redSDCardInserted02 = true;
                System.out.println("Red SD Card 2 placed into slot");
                break;

            case "p paper":
                this.paperPlaced = true;
                System.out.println("Printer paper placed into machine");
                break;

            case "f screen":
                this.screenFailed = true;
                System.out.println("Touch screen failure status set to TRUE.");
                break;

            case "t screen":
                this.screenTampered = true;
                System.out.println("Touch screen tamper status set to TRUE.");
                break;

            case "help":
                printHelp();
                break;

            default:
                System.out.println("Unknown command. Type 'help' for a list of commands.");
                break;
        }
    }

    /**
     * Prints the list of available commands and their descriptions.
     * This method is called when the user types 'help'.
     */
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  t printer         - Set printer tampered status to TRUE.");
        System.out.println("  f printer         - Set printer failure status to TRUE.");
        System.out.println("  t latch           - Set latch tampered status to TRUE.");
        System.out.println("  f latch           - Set latch failure status to TRUE.");
        System.out.println("  t cardreader      - Set card reader tampered status to TRUE.");
        System.out.println("  f cardreader      - Set card reader failure status to TRUE.");
        System.out.println("  i admin           - Simulate insertion of an admin card (code: A123).");
        System.out.println("  i voter           - Simulate insertion of a voter card (code: V456).");
        System.out.println("  i other           - Simulate insertion of another type of card (code: DOGE).");
        System.out.println("  t bluedriver      - Set Blue SDCardDriver tampered status to TRUE.");
        System.out.println("  f bluedriver      - Set Blue SDCardDriver failure status to TRUE.");
        System.out.println("  t reddriver1      - Set Red SDDriver 01 tampered status to TRUE.");
        System.out.println("  f reddriver1      - Set Red SDDriver 01 failure status to TRUE.");
        System.out.println("  t reddriver2      - Set Red SDDriver 02 tampered status to TRUE.");
        System.out.println("  f reddriver2      - Set Red SDDriver 02 failure status to TRUE.");
        System.out.println("  p bluesd          - Simulate placing a Blue SD card into the slot.");
        System.out.println("  p redsd1          - Simulate placing a Red SD card 1 into the slot.");
        System.out.println("  p redsd2          - Simulate placing a Red SD card 2 into the slot.");
        System.out.println("  p paper           - Simulate placing paper into the printer.");
        System.out.println("  f screen          - Set screen failure status to TRUE.");
        System.out.println("  t screen          - Set screen tampered status to TRUE.");
        System.out.println("  help              - Display this list of commands and their descriptions.");
    }
}
