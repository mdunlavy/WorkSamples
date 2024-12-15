package Software;

import Devices.*;

public class Main {
    public static void main(String[] args) {
        // Device Simulator Instantiation
        DeviceSimulatorCLI deviceSimulatorCLI = new DeviceSimulatorCLI();
        Thread deviceSimulatorCLIThread = new Thread(deviceSimulatorCLI);
        deviceSimulatorCLIThread.start();

        // Device Instantiation
        Printer printer = new Printer("src/ExternalMemory/PrinterRoll.txt", deviceSimulatorCLI);
        Latch latch = new Latch(deviceSimulatorCLI);
        CardReader cardReader = new CardReader(deviceSimulatorCLI);
        SDCardDriver blueSDCardDriver = new SDCardDriver("src/ExternalMemory/pokemon_ballot.xml", true,
                deviceSimulatorCLI, 1);
        SDCardDriver redSDCardDriver01 = new SDCardDriver("src/ExternalMemory/savedballots01.txt", false,
                deviceSimulatorCLI, 2);
        SDCardDriver redSDCardDriver02 = new SDCardDriver("src/ExternalMemory/savedballots02.txt", false,
                deviceSimulatorCLI, 3);
        ScreenController screenController = new ScreenController(deviceSimulatorCLI);
        TamperSensor tamperSensor = new TamperSensor(printer, latch, cardReader, blueSDCardDriver, redSDCardDriver01,
                redSDCardDriver02, screenController);

        // Voting Machine Software Instantiation
        VoteManager voteManager = new VoteManager(printer, latch, cardReader, blueSDCardDriver, redSDCardDriver01,
                redSDCardDriver02, tamperSensor, screenController);
        Thread voteManagerThread = new Thread(voteManager);
        voteManagerThread.start();
    }
}
