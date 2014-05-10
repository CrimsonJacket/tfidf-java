package main;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author crimson
 */
public class Main {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        DocumentParser dp = new DocumentParser();
        dp.parseFiles("dataFiles");
        //dp.printDocs();
        dp.getTerms();
        dp.tfIdfCalculator();
        dp.sortIndex();
        dp.printIndex();

    }
}
