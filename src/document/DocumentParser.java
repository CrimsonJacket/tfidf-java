package document;

import calculation.CosineSimilarity;
import calculation.SearchTermExpansion;
import calculation.StopWord;
import calculation.Synonym;
import calculation.TfIdfCalculator;
import calculation.WordStemming;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import main.TfIdf_Frame;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniel
 */
public class DocumentParser {

    public static HashMap<String,Document> docSet = null;
    public static List<String> allTerms = null; //store all terms
    public static HashMap<String, Double> tfidfMap = null;
    public static String filePath;
    public static boolean enableCosine;
    public static boolean enableStopWord;
    public static boolean enableWordStem;
    public static boolean enableWordExpansion;
    public static boolean enableSynonym;
    public static boolean enableHyponym;

    public DocumentParser(boolean cosine, boolean stopword, boolean wordstem, boolean wordexp, boolean syn, boolean hyp, String filePath) {
        DocumentParser.enableCosine = cosine;
        DocumentParser.enableStopWord = stopword;
        DocumentParser.enableWordStem = wordstem;
        DocumentParser.enableWordExpansion = wordexp;  
        DocumentParser.enableSynonym = syn;
        DocumentParser.enableHyponym = hyp;
        DocumentParser.filePath = filePath;
        TfIdf_Frame.setSettingsMessage("Folder Path: " + DocumentParser.filePath);
        TfIdf_Frame.appendSettingsMessage("StopWord Path: " + StopWord.fileName);
        TfIdf_Frame.appendSettingsMessage("");
        TfIdf_Frame.appendSettingsMessage("Cosine Similarity: " + enableCosine);
        TfIdf_Frame.appendSettingsMessage("Stop Word: " + enableStopWord);
        TfIdf_Frame.appendSettingsMessage("Word Stemming: " + enableWordStem);
        TfIdf_Frame.appendSettingsMessage("Search Expansion: " + enableWordExpansion);
    }

    public void parseFiles() throws FileNotFoundException, IOException {
        File[] allfiles = new File(filePath).listFiles();
        docSet = new HashMap<>();
        TfIdf_Frame.setMessage("Initializing HashMaps");
        for (File f : allfiles) {
            Document doc = new Document(f);
            docSet.put(f.getName(), doc);
        }
        TfIdf_Frame.appendMessage("[+] Loaded " + docSet.size() + " files");
        TfIdf_Frame.appendMessage("[+] Documents Loaded");
    }

    public void setTerms(String userInput) throws IOException {
        allTerms = new ArrayList<>();
        // Search Term Expansion
        if(enableWordExpansion){
            userInput = SearchTermExpansion.SearchTermExpansion(userInput);
        }       
        if(enableSynonym){
            userInput = Synonym.getSynonym(userInput);
        }
        if(enableHyponym){
            userInput = Synonym.getHyponym(userInput);
        }
        String[] terms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
        for (String t : terms) {
            //Stop Words
            if(enableStopWord){
                if(StopWord.hs.contains(t)){
                    break;//ignores the term & does not add it to allTerms
                }
            }
            //Word Stem
            if (enableWordStem) {
                t = WordStemming.implementStem(t);
            }
            //adds the term into the final Term(s) List
            if (!allTerms.contains(t)) {
                allTerms.add(t);
            }
        }

    }

    public void tfIdfCalculator() {
        TfIdf_Frame.setMessage("Calculating TF-IDF Vectors & TF-IDF Values...");
        tfidfMap = new HashMap<>();
        for (Map.Entry<String,Document> doc : docSet.entrySet()) {
            doc.getValue().tfidfVectors = new HashMap<>();
            double tf = 0.0;
            double idf = 0.0;
            double finalTfIdf = 0.0;         
            for (String term : allTerms) {
                if(enableWordStem){
                    term = WordStemming.implementStem(term);
                }
                tf = new TfIdfCalculator().calculateTF(doc.getValue().getWordMaps(), term, doc.getValue().getWordCount());
                idf = new TfIdfCalculator().calculateIDF(term, tf);
                finalTfIdf += tf*idf;            
                doc.getValue().tfidfVectors.put(term, tf*idf);
            }
            doc.getValue().tfidfVectors = Document.sortMaps(doc.getValue().tfidfVectors);
            tfidfMap.put(doc.getValue().getFileName(), finalTfIdf);
        }
        TfIdf_Frame.appendMessage("[+] Populated TF-IDF Vectors");
        TfIdf_Frame.appendMessage("[+] Calculated TF-IDF Values");
    }

    public static void setEnableSynonym(boolean enableSynonym) {
        DocumentParser.enableSynonym = enableSynonym;
    }

    public static void setEnableHyponym(boolean enableHyponym) {
        DocumentParser.enableHyponym = enableHyponym;
    }
    
    public void cosineSimilarityCalculator(){
        TfIdf_Frame.appendMessage("Fetching TF-IDF Vectors & Calulating Cosine Similarity...");
        for(Map.Entry<String, Document> doc : docSet.entrySet()){
            HashMap<String, Double> cosineMap = new HashMap<>();
            for(Map.Entry<String, Document> docToCompare : docSet.entrySet()){
                double cosineSimilarity = 0.0;
                if(doc.getValue().getFileName().equalsIgnoreCase(docToCompare.getValue().getFileName())){
                    break;
                }
                cosineSimilarity = new CosineSimilarity().calculateCosineSimilarity(doc.getValue().tfidfVectors, docToCompare.getValue().tfidfVectors);
                cosineMap.put(docToCompare.getValue().getFileName(), cosineSimilarity);
            }
            doc.getValue().setCosineMaps(cosineMap);
            doc.getValue().cosineMaps = doc.getValue().sortMaps(cosineMap);
        }
        TfIdf_Frame.appendMessage("[+] Calculated Cosine Similarity Values");
    }

    public void sortIndex() {
        List<Map.Entry<String, Double>> entries = new LinkedList<>(tfidfMap.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(entries);//descending order
        HashMap<String, Double> sortedMap = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Double> entry : entries) {
            if (entry.getValue() != 0.000000) {
                sortedMap.put(entry.getKey(), entry.getValue());
                count++;
            }
            if (count > 9) {
                break;
            }
        }
        tfidfMap = sortedMap;
        TfIdf_Frame.appendMessage("[+] Sorted Index");
    }

    public static void setFilePath(String filePath) {
        DocumentParser.filePath = filePath;
    }

    public static void setEnableCosine(boolean enableCosine) {
        DocumentParser.enableCosine = enableCosine;
    }

    public static void setEnableStopWord(boolean enableStopWord) {
        DocumentParser.enableStopWord = enableStopWord;
    }

    public static void setEnableWordStem(boolean enableWordStem) {
        DocumentParser.enableWordStem = enableWordStem;
    }

    public static void setEnableWordExpansion(boolean enableWordExpansion) {
        DocumentParser.enableWordExpansion = enableWordExpansion;
    }

    public ListModel populateJList() {
        DefaultListModel model = new DefaultListModel();
        for (Map.Entry<String, Double> entry : tfidfMap.entrySet()) {
            model.addElement(entry.getKey());
        }
        return model;
    }    

    private static String initDivider() {
        String s = "=";
        int n = 100;
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        sb.append("\n");
        return sb.toString();
    }

    public void printIndex() {
        String column0 = "No.";
        String column1 = "File Name";
        String column2 = "TF-IDF value";
        String column3 = "Similar Files";
        String column4 = "Cosine Similarity Value";

        StringBuilder finalBuilder = new StringBuilder();

        finalBuilder.append("Results:\n");
        finalBuilder.append(initDivider());

        finalBuilder.append(String.format("%-3s %-30s %10s %16s %35s %n",
                column0,
                column1,
                column2,
                column3,
                column4)
        );
        finalBuilder.append(initDivider());
        int count = 1;
        for (Map.Entry<String, Double> entry : tfidfMap.entrySet()) {
            finalBuilder.append(String.format("%-3d %-30s %10.6f %n", count,entry.getKey(), entry.getValue()));
            count++;
            if (enableCosine) {
                for(Map.Entry<String, Document> doc : docSet.entrySet()){
                    if (entry.getKey().equalsIgnoreCase(doc.getValue().getFileName())) {
                        finalBuilder.append(doc.getValue().getCosineMaps());
                    }
                }
            }

        }
        TfIdf_Frame.appendMessage(finalBuilder.toString());
    }

    public void printDocs() {
        for(Map.Entry<String, Document> entry : docSet.entrySet()){
            entry.getValue().printWordMaps();
        }
    }
}
