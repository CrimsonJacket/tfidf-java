package main;

import calculation.StopWord;
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
        StopWord stopWord = new StopWord("StopWord.csv");
        // parseFiles Function may require StopWord.csv Parameter
        dp.parseFiles("dataFiles.original");
        
        dp.printDocs();
        //dp.getTerms();
        //dp.tfIdfCalculator();
        //dp.sortIndex();
        //dp.printIndex();
    }
}
