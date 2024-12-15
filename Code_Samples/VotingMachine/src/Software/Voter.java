package Software;

import Devices.*;
import Software.Classes.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Voter implements Runnable {

    private Ballot sampleBallot;

    private final Printer printer;
    private final SDCardDriver blueSDCardDriver;
    private final SDCardDriver redSDCardDriver01;
    private final SDCardDriver redSDCardDriver02;
    private final ScreenController screenController;

    /**
     * Constructor for the Voter class.
     * 
     * @param printer           the printer
     * @param blueSDCardDrive   the blue SD card driver
     * @param redSDCardDriver01 the red SD card driver 01
     * @param redSDCardDriver02 the red SD card driver 02
     * @param screenController  the screen controller
     */
    public Voter(Printer printer, SDCardDriver blueSDCardDrive, SDCardDriver redSDCardDriver01,
            SDCardDriver redSDCardDriver02, ScreenController screenController) {
        this.printer = printer;
        this.blueSDCardDriver = blueSDCardDrive;
        this.redSDCardDriver01 = redSDCardDriver01;
        this.redSDCardDriver02 = redSDCardDriver02;
        this.screenController = screenController;
        this.sampleBallot = null;
    }

    /**
     * Starts voter thread.
     */
    @Override
    public void run() {
        try {
            openAccessToDevices();
            readAndInitializeSampleBallot();
            ArrayList<Template> tem = Conversion.convertToTemplate(sampleBallot);
            screenController.showScreen(tem);
            while (true) {
                if (screenController.isFinished()) {
                    break;
                }
                System.out.println("Not Finished");
                Thread.sleep(5000);
            }
            ArrayList<Template> templatesAfter = this.screenController.returnTemplates();
            Ballot ballotAfter = Conversion.convertToBallot(templatesAfter);
            String[] votesAfter = Conversion.votesInfoToString(ballotAfter);
            saveAndPrintVotes(votesAfter);
            closeAccessToDevices();
            Template welcomeScreen = new Template("Welcome", "Welcome to the Voting System!", 1, new ArrayList<>());
            ArrayList<Template> screens = new ArrayList<>();
            screens.add(welcomeScreen);
            screenController.showScreen(screens);
        } catch (InterruptedException e) {
            System.out.println("Voter interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Used to read and initialize the sample ballot
     */
    private void readAndInitializeSampleBallot() {
        if (this.sampleBallot == null) {
            String xml_text = String.join("", this.blueSDCardDriver.readLines());
            this.sampleBallot = makeBallotFromXML(xml_text);
        }
    }

    /**
     * From a xml makes the Template Ballot
     * 
     * @param xml String
     * @return Ballot
     */
    private Ballot makeBallotFromXML(String xml) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder;
        Document document;
        InputSource is;
        try {
            is = new InputSource(new StringReader(xml));
            docbuilder = factory.newDocumentBuilder();
            document = docbuilder.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        document.getDocumentElement().normalize();

        Element root = document.getDocumentElement();

        String electionName = root.getElementsByTagName("electionName").item(0).getTextContent();
        String startDate = root.getElementsByTagName("startDate").item(0).getTextContent();
        String endDate = root.getElementsByTagName("endDate").item(0).getTextContent();
        String startForDay = root.getElementsByTagName("startForDay").item(0).getTextContent();
        String endForDay = root.getElementsByTagName("endForDay").item(0).getTextContent();

        ArrayList<Proposition> propositionsList = new ArrayList<>();

        NodeList propositions = root.getElementsByTagName("proposition");
        for (int i = 0; i < propositions.getLength(); i++) {
            Element proposition = (Element) propositions.item(i);
            String propName = proposition.getElementsByTagName("propName").item(0).getTextContent();
            String propDesc = proposition.getElementsByTagName("propDesc").item(0).getTextContent();
            ArrayList<Option> optionsList = new ArrayList<>();
            NodeList options = proposition.getElementsByTagName("option");
            for (int j = 0; j < options.getLength(); j++) {
                optionsList.add(new Option(options.item(j).getTextContent()));
            }
            int numChoices = Integer.parseInt(proposition.getElementsByTagName("numChoices").item(0).getTextContent());
            Proposition propositionRecord = new Proposition(propName, propDesc, numChoices, optionsList);
            propositionsList.add(propositionRecord);
        }

        return new Ballot(electionName, LocalDate.parse(startDate), LocalDate.parse(endDate),
                LocalTime.parse(startForDay), LocalTime.parse(endForDay), propositionsList);
    }

    /**
     * Will take the votes separated into an array of Strings and print and save
     * them
     * 
     * @param votes String[] votes
     */
    private void saveAndPrintVotes(String[] votes) {
        for (String vote : votes) {
            this.printer.printLine(vote);
            this.redSDCardDriver01.writeLine(vote);
            this.redSDCardDriver02.writeLine(vote);
        }
    }

    /**
     * Open access to the SD drivers and printer
     */
    private void openAccessToDevices() {
        this.blueSDCardDriver.openSDCardAccess();
        this.redSDCardDriver01.openSDCardAccess();
        this.redSDCardDriver02.openSDCardAccess();
        this.printer.openPrinterAccess();
    }

    private void closeAccessToDevices() {
        this.blueSDCardDriver.closeSDCardAccess();
        this.redSDCardDriver01.closeSDCardAccess();
        this.redSDCardDriver02.closeSDCardAccess();
        this.printer.closePrinterAccess();
    }
}
