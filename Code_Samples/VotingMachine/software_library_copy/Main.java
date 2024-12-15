import Drivers.Latch;
import Drivers.Printer;
import Drivers.WindowManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ///// EXAMPLE FOR DEMONSTRATION //////

        WindowManager windowManager = new WindowManager();
        Latch latch = new Latch();
        Printer printer = null;
        String input;

        // Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Main loop for user commands
        while (true) {
            System.out.println("Enter initial command. printer / tamper / latch / exit");
            input = scanner.nextLine();
            boolean value = false;

                // if tamper is selected do this
                if (input.equalsIgnoreCase("tamper")) {
                    tamperHelper(value, scanner, windowManager);
                }

                // If printer is selected do this
                else if (input.equalsIgnoreCase("printer")) {
                    printerHelper(value, scanner, windowManager,printer);
                }

                // If latch is selected do this
                else if (input.equalsIgnoreCase("latch")){
                    latchHelper(value, scanner, windowManager, printer, latch);
                }

                else if (input.equalsIgnoreCase("Exit")) {
                System.out.println("Exiting the Entire Program.");
                scanner.close();
                return;
            } else {
                //System.out.println("Invalid command. Try again");
            }
        }
    }

    public static void latchHelper(boolean value, Scanner scanner, WindowManager windowManager ,Printer printer, Latch latch){
        while(!value){
            System.out.println("Enter command latch | window(latch value in windowManager) | quit");
            String input = scanner.nextLine();

            switch (input){
                case "latch":
                    System.out.println("Latch value: " + windowManager.gethasLatchSensorFailed());
                    break;
                case "failure" :
                    windowManager.userInput(scanner);
                    break;
                case "window":
                    System.out.println("windowManager value: " + windowManager.gethasLatchSensorFailed());
                    break;
                case "quit":
                    System.out.println("Exiting Latch");
                    value = true;
                    break;
                default:
            }
        }
    }

    public static void printerHelper(boolean value, Scanner scanner, WindowManager windowManager, Printer printer){
        while (!value) {
            System.out.print("Enter command (pl(Print Line) / pon(Printer On) / poff(Printer Off) / pe(Print Empty Line) / failure/ quit): ");
            String input = scanner.nextLine();

            switch (input) {
                case "pl":
                    if (windowManager.getHasPrinterFailed()){
                        System.out.println("Printer has failed");
                    } else if (printer != null) {
                        System.out.print("What to print: ");
                        String textToPrint = scanner.nextLine();
                        printer.printLine(textToPrint);
                    } else {
                        System.out.println("Printer is not on. Please use 'pon'");
                    }
                    break;
                case "pon":
                    System.out.println("Enter output file name with the extension: ");
                    String fileName = scanner.nextLine();
                    printer = new Printer(fileName);
                    printer.on();
                    break;
                case "poff":
                    if (printer != null) {
                        printer.off();
                    } else {
                        System.out.println("Printer is not on.");
                    }
                    break;
                case "pe":
                    if (printer != null) {
                        printer.printEmptyLine();
                    } else {
                        System.out.println("Printer is not on.");
                    }
                    break;
                case "quit":
                    System.out.println("Exiting Printer");
                    value = true;
                    break;
                case "failure":
                    //force the failure
                    windowManager.userInput(scanner); // Force the fail with option 2
                    break;
                default:
            }
        }
    }


    public static void tamperHelper(boolean value, Scanner scanner, WindowManager windowManager){
        System.out.println("Enter the file name and extension(Will be saved)");
        String tamperFileName = scanner.nextLine();
        Printer tamperPrinter = new Printer(tamperFileName);
        tamperPrinter.on();


        while(!value) {
            System.out.print("Enter command: iterations / failure / quit): ");
            String input = scanner.nextLine();

            switch (input) {
                case "iterations":
                    System.out.println("Enter the number of iterations");
                    int iterations = scanner.nextInt();
                    scanner.nextLine();
                    for (int i = 0; i < iterations; i++) {
                        tamperPrinter.printLine("Tamper Sensor Status: " + windowManager.getHasTamperSensorFailed());
                    }
                    break;

                case "failure":
                    //force the failure
                    windowManager.userInput(scanner); // Force the fail with option 2
                    //System.out.println("THIS IS: " + windowManager.isHasTamperSensorFailed());
                    //System.out.println("Force Tamper Sensor Failure");
                    break;

                case "quit":
                    System.out.println("Exiting Tamper Sensor.");
                    tamperPrinter.off();
                    return;
                default:

            }
        }
    }
}
