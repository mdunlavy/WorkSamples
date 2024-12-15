package Ballot;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class ExtractInfoXML {

    public static void printBallot(Ballot ballot){
        System.out.println("Election name: " + ballot.electionName());
        System.out.println("start date: " + ballot.startDate());
        System.out.println("end date: " + ballot.endDate());
        System.out.println("start time: " + ballot.startForDay());
        System.out.println("end time: " + ballot.endForDay());
        for (Proposition p : ballot.propositions()) {
            System.out.println("---------");
            System.out.println("Proposition name: "+p.propName());
            System.out.println("Proposition description: "+p.propDesc());
            System.out.println("Select from: " + p.selectableOptions());
            for (Option o : p.options()) {
                System.out.println("   "+o.description() + "  " + o.isSelected());
            }
            System.out.println("---------");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            scanner = new Scanner( new File("test.xml") );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String text = scanner.useDelimiter("\\A").next();

        Ballot ballot = makeBallotFromXML(text);
        printBallot(ballot);

        ballot.propositions().get(0).options().get(0).select();
        System.out.println();
        printBallot(ballot);
    }



    public static Ballot makeBallotFromXML(String xml){
        // Load and parse the XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docbuilder = null;
        Document document = null;
        InputSource is = null;
        try {
            is = new InputSource(new StringReader(xml));
            docbuilder = factory.newDocumentBuilder();
            document = docbuilder.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        // Normalize the XML structure to ensure whitespace doesn't mess anything up
        document.getDocumentElement().normalize();

        // Root element
        Element root = document.getDocumentElement();

        // Extract election information
        String electionName = root.getElementsByTagName("electionName").item(0).getTextContent();
        String startDate = root.getElementsByTagName("startDate").item(0).getTextContent();
        String endDate = root.getElementsByTagName("endDate").item(0).getTextContent();
        String startForDay = root.getElementsByTagName("startForDay").item(0).getTextContent();
        String endForDay = root.getElementsByTagName("endForDay").item(0).getTextContent();

        ArrayList<Proposition> propositionsList = new ArrayList<>(); // List to hold all Proposition records

        NodeList propositions = root.getElementsByTagName("proposition");
        for (int i = 0; i < propositions.getLength(); i++) {
            Element proposition = (Element) propositions.item(i);
            String propName = proposition.getElementsByTagName("propName").item(0).getTextContent();
            String propDesc = proposition.getElementsByTagName("propDesc").item(0).getTextContent();
            // Collect options for each proposition
            ArrayList<Option> optionsList = new ArrayList<>();
            NodeList options = proposition.getElementsByTagName("option");
            for (int j = 0; j < options.getLength(); j++) {
                optionsList.add(new Option(options.item(j).getTextContent()));
            }
            int numChoices = Integer.parseInt(proposition.getElementsByTagName("numChoices").item(0).getTextContent());
            // Create a new Proposition record and add it to the list
            Proposition propositionRecord = new Proposition(propName, propDesc,numChoices, optionsList);
            propositionsList.add(propositionRecord);
        }

        return new Ballot(electionName, LocalDate.parse(startDate),LocalDate.parse(endDate),
                LocalTime.parse(startForDay),LocalTime.parse(endForDay),propositionsList);
    }
}

