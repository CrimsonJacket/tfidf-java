package main;

import document.DocumentParser;
import java.io.FileNotFoundException;
import java.io.IOException;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Daniel
 */
public class Main {

    public static void main(String args[]) throws FileNotFoundException, IOException {
        DocumentParser dp = new DocumentParser();
        dp.parseFiles("dataFiles");
        dp.printDocs();
        //dp.getTerms();
        //dp.tfIdfCalculator();
        //dp.sortIndex();
        //dp.printIndex();
    }
}
