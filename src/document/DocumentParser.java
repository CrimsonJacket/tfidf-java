package document;



import calculation.CosineSimilarity;
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
import calculation.TfIdfCalculator;
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
    public HashMap<String, Double[]> tfidfVectorMap=  new HashMap<>();
    public static HashMap<String, Double> tfidfMap = new HashMap<>();

    public void parseFiles(String filePath) throws FileNotFoundException, IOException {
        File[] allfiles = new File(filePath).listFiles();
        docArray = new ArrayList<>(allfiles.length);
        System.out.println("Initializing HashMaps");
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
        System.out.println("Calculating TfIdf Vectors & Final Values...");
        for(Document doc : docArray){
            double tf = 0.0;
            double idf = 0.0;
            Double[] tfidfVectors = new Double[allTerms.size()];
            int count = 0;
            for(String term: allTerms){
                tf = new TfIdfCalculator().calculateTF(doc.getWordMaps(), term, doc.getWordCount());
                idf = new TfIdfCalculator().calculateIDF(term,tf);
                tfidfVectors[count] = tf*idf; //tf-idf value for 1 term only
                count++;
            }
            tfidfVectorMap.put(doc.getFileName(),tfidfVectors);// Vectors are needed for Cosine Similarity Calculation
        }
        
        System.out.println("[+] Populated TF-IDF Vectors");
        for(Map.Entry<String,Double[]> entry : tfidfVectorMap.entrySet()){
            double finalTfIdf = 0.0;
            Double[] tmpArr = entry.getValue();
            for(double tfidf : tmpArr){
                finalTfIdf+=tfidf; //total of tf-idf values for each document
            }
            
            tfidfMap.put(entry.getKey(), finalTfIdf);// Calculates final TfIdf value of all documents
        }
        System.out.println("[+] Calculated TF-IDF Values");
    }
    
    public void cosineSimilarityCalculator(){
        System.out.println("Fetching TF-IDF values & Calulating Cosine Similarity...");
        for(Document doc : docArray){
            HashMap<String, Double> cosineMap = new HashMap<>();
            Double[] docVector = tfidfVectorMap.get(doc.getFileName());
            for(Map.Entry<String,Double[]> entry: tfidfVectorMap.entrySet()){  
                double cosineSimilarity = 0.0;
                if(!entry.getKey().equalsIgnoreCase(doc.getFileName())){
                    Double[] vectorToCheck = entry.getValue();
                    cosineSimilarity = new CosineSimilarity().getCosineSimilarity(docVector, vectorToCheck);
                    cosineMap.put(entry.getKey(), cosineSimilarity);
                }
            }
            doc.setCosineMaps(cosineMap);
            doc.sortCosineMap();
        }
        System.out.println("[+] Calculated Cosine Similarity Values");
    }
    
    public void sortIndex(){
        List<Map.Entry<String,Double>> entries = new LinkedList<>(tfidfMap.entrySet());   
        
        Collections.sort(entries, new Comparator<Map.Entry<String,Double>>() {
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(entries);//descending order
        HashMap<String,Double> sortedMap = new LinkedHashMap<>(); 
        int count = 0;
        for(Map.Entry<String,Double> entry: entries){
            sortedMap.put(entry.getKey(), entry.getValue());
            count++;
            if(count>9){
                break;
            }
        } 
        tfidfMap = sortedMap;
        System.out.println("[+] Sorted Index");
    }
    
    private static String initDivider() {
        String s = "=";
        int n = 100;
        StringBuilder sb = new StringBuilder(s.length() * n);
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public void printIndex(){
        String column1 = "File Name";
        String column2 = "TF-IDF value";
        String column3 = "Similar Files";
        String column4 = "Cosine Similarity Value";
        System.out.printf("%-30s %-10s %15s %40s %n", column1, column2, column3, column4);
        System.out.println(initDivider());
        for (Map.Entry<String, Double> entry: tfidfMap.entrySet()){
            if(entry.getValue()!=0.000000){
               System.out.printf("%-30s %10.6f %n",entry.getKey(),entry.getValue());            
                for(Document doc: docArray){
                    if(entry.getKey().equalsIgnoreCase(doc.getFileName())){
                        doc.printCosineMaps();
                    }
                } 
            }
            
        }
    }
    
    public void printDocs() {
        for (Document entry : docArray) {
            entry.printWordMaps();
        }
    }  
}
