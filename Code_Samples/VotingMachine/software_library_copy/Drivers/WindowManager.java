package Drivers;


import java.util.Scanner;


public class WindowManager {


    private boolean hasPrinterFailied;
    private boolean hasTamperSensorFailed;
    private boolean hasLatchSensorFailed;
    private boolean hasSDCardDriverFailed;
    private boolean hasCardReaderFailed;


    public WindowManager(){
        this.hasPrinterFailied = false;
        this.hasTamperSensorFailed = false;
        this.hasCardReaderFailed = false;
        this.hasLatchSensorFailed = false;
        this.hasSDCardDriverFailed = false;
    }


    //simulate user input and the responses
    public void userInput(Scanner scanner){
        System.out.println("1. Printer Failure | 2. Tamper Sensor Failure | 3. Card Reader Failure");
        System.out.println("4. Latch Sensor Failure | 5. SDCard Driver Failure");
        String input = scanner.nextLine();

        // Force printer failure
        if(input.equals("1")){
            hasPrinterFailied = true;
        }else if (input.equals("2")){
            hasTamperSensorFailed = true;
        }else if(input.equals("3")){
            hasCardReaderFailed = true;
        }else if(input.equals("4")){
            hasLatchSensorFailed = true;
        }else if(input.equals("5")){
            hasSDCardDriverFailed = true;
        }
    }


    public boolean getHasTamperSensorFailed(){ return hasTamperSensorFailed; }
    public boolean getHasPrinterFailed(){ return hasPrinterFailied; }
    public boolean gethasLatchSensorFailed(){ return hasLatchSensorFailed;}
    public boolean getHasSDCardDriverFailed(){ return hasSDCardDriverFailed; }
    public boolean getHasCardReaderFailed(){ return hasCardReaderFailed; }
}
