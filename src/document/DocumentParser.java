package document;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import calculation.TfIdf;
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
    private List<String> allTerms = new ArrayList<>(); //store all terms
    private HashMap<String, Double> indexMap = new HashMap<>();

    public void printDocs() {
        for (Document entry : docArray) {
            entry.printWordMaps();
        }
    }
    
    public void parseFiles(String filePath) throws FileNotFoundException, IOException {
        File[] allfiles = new File(filePath).listFiles();
        docArray = new ArrayList<>(allfiles.length);
        System.out.println("Initializing HashMap");
        for (File f : allfiles) {
            Document doc = new Document(f);
            docArray.add(doc);           
        }      
        System.out.println("[+] Loaded " + docArray.size() + " files");  
        System.out.println("[+] Documents Loaded");
    }

    public void getTerms() throws IOException {
        allTerms = new ArrayList<>(); //
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter Search Term(s): ");
        String userInput = br.readLine();
        String[] terms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
        for (String t : terms) {
            if (!allTerms.contains(t)) {
                
                allTerms.add(t);
            }
        }
    }

    public void tfIdfCalculator() {
        System.out.println("Fetching Documents and Calculating tfIdf...");
        for(Document doc : docArray){
            double tf = 0;
            double idf = 0;
            double tfidf = 0;;
            for(String term: allTerms){
                tf = new TfIdf().calculateTF(doc.getWordMaps(), term, doc.getWordCount());
                idf = new TfIdf().calculateIDF(term,tf);
                tfidf += tf*idf;
            }
            indexMap.put(doc.getFileName(),tfidf);
        }
    }
    
    public void sortIndex(){
        List<Map.Entry<String,Double>> entries = new LinkedList<>(indexMap.entrySet());      
        Collections.sort(entries, new Comparator<Map.Entry<String,Double>>() {
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(entries);//descending order
        HashMap<String,Double> sortedMap = new LinkedHashMap<>();     
        for(Map.Entry<String,Double> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
        } 
        indexMap = sortedMap;
        System.out.println("[+] Sorted Index");
    }

    public void printIndex(){
        int count = 0;
        for (Map.Entry<String, Double> entry: indexMap.entrySet()){
            System.out.println(entry.getKey() + "\t" + entry.getValue());
            count++;
            if(count>9){
                break;
            }
        }
    }

}
