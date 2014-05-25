package document;

import calculation.CosineSimilarity;
import calculation.SearchTermExpansion;
import calculation.StopWord;
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

    public static List<Document> docArray = null;
    public static List<String> allTerms = null; //store all terms
    public HashMap<String, Double[]> tfidfVectorMap = null;
    public static HashMap<String, Double> tfidfMap = null;
    public static String filePath;
    public static boolean enableCosine;
    public static boolean enableStopWord;
    public static boolean enableWordStem;
    public static boolean enableWordExpansion;

    public DocumentParser(boolean cosine, boolean stopword, boolean wordstem, boolean wordexp, String filePath) {
        DocumentParser.enableCosine = cosine;
        DocumentParser.enableStopWord = stopword;
        DocumentParser.enableWordStem = wordstem;
        DocumentParser.enableWordExpansion = wordexp;  
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
        docArray = new ArrayList<>(allfiles.length);
  
        TfIdf_Frame.setMessage("Initializing HashMaps");
        for (File f : allfiles) {
            Document doc = new Document(f);
            docArray.add(doc);
        }
        TfIdf_Frame.appendMessage("[+] Loaded " + docArray.size() + " files");
        TfIdf_Frame.appendMessage("[+] Documents Loaded");
    }

    public void setTerms(String userInput) throws IOException {
        allTerms = new ArrayList<>();
        String[] terms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");       
        for (String t : terms) {
            //Search Expansion
            
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
            if (!allTerms.contains(t)) {
                allTerms.add(t);
            }
        }
    }

    public void tfIdfCalculator() {
        TfIdf_Frame.setMessage("Calculating TF-IDF Vectors & TF-IDF Values...");
        tfidfVectorMap = new HashMap<>();
        tfidfMap = new HashMap<>();
        for (Document doc : docArray) {
            double tf = 0.0;
            double idf = 0.0;
            double finalTfIdf = 0.0;
            Double[] tfidfVectors = new Double[allTerms.size()];
            int count = 0;           
            for (String term : allTerms) {
                if(enableWordStem){
                    term = WordStemming.implementStem(term);
                }
                tf = new TfIdfCalculator().calculateTF(doc.getWordMaps(), term, doc.getWordCount());
                idf = new TfIdfCalculator().calculateIDF(term, tf);
                finalTfIdf += tf*idf;
                tfidfVectors[count] = tf * idf;
                count++;                
                doc.tfidfVectors.put(term, tf*idf);
            }
            tfidfVectorMap.put(doc.getFileName(), tfidfVectors);// Vectors are needed for Cosine Similarity Calculation
            tfidfMap.put(doc.getFileName(), finalTfIdf);
        }
        TfIdf_Frame.appendMessage("[+] Populated TF-IDF Vectors");
        TfIdf_Frame.appendMessage("[+] Calculated TF-IDF Values");
    }
    
    public void cosineSimilarityCalculator(){
        TfIdf_Frame.appendMessage("Fetching TF-IDF Vectors & Calulating Cosine Similarity...");
        for(Document doc : docArray){
            HashMap<String, Double> cosineMap = new HashMap<>();
            for(Document docToCompare : docArray){
                double cosineSimilarity = 0.0;
                if(doc.getFileName().equalsIgnoreCase(docToCompare.getFileName())){
                    break;
                }
                cosineSimilarity = new CosineSimilarity().calculateCosineSimilarity(doc.tfidfVectors, docToCompare.tfidfVectors);
                cosineMap.put(docToCompare.getFileName(), cosineSimilarity);
            }
            doc.setCosineMaps(cosineMap);
            doc.sortCosineMap();
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

    private static String initDivider() {
        String s = "=";
        int n = 95;
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        sb.append("\n");
        return sb.toString();
    }

    public void printIndex() {
        String column1 = "File Name";
        String column2 = "TF-IDF value";
        String column3 = "Similar Files";
        String column4 = "Cosine Similarity Value";

        StringBuilder finalBuilder = new StringBuilder();

        finalBuilder.append("Results:\n\n");
        finalBuilder.append(initDivider());

        finalBuilder.append(String.format("%-30s %10s %15s %35s %n",
                column1,
                column2,
                column3,
                column4));
        finalBuilder.append(initDivider());

        for (Map.Entry<String, Double> entry : tfidfMap.entrySet()) {
            finalBuilder.append(String.format("%-30s %10.6f %n", entry.getKey(), entry.getValue()));
            if (enableCosine) {
                for (Document doc : docArray) {
                    if (entry.getKey().equalsIgnoreCase(doc.getFileName())) {
                        finalBuilder.append(doc.getCosineMaps());
                    }
                }
            }

        }
        TfIdf_Frame.appendMessage(finalBuilder.toString());
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

    public void printDocs() {
        for (Document entry : docArray) {
            entry.printWordMaps();
        }
    }

    public ListModel populateJList() {
        DefaultListModel model = new DefaultListModel();
        for (Map.Entry<String, Double> entry : tfidfMap.entrySet()) {
            model.addElement(entry.getKey());
        }
        return model;
    }
}
