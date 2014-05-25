package document;

import calculation.StopWord;
import calculation.WordStemming;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Daniel
 */
public class Document {

    private final String fileName;
    public HashMap<String, Double> cosineMaps = new HashMap<>();
    private HashMap<String, Integer> wordMaps = new HashMap<>();
    public HashMap<String, Double> tfidfVectors = new HashMap<>();
    private int wordCount;

    public Document(File f) throws FileNotFoundException, IOException {
        this.fileName = f.getName();
        BufferedReader in = null;
        if (f.getName().endsWith(".txt")) {           
            in = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = in.readLine()) != null) {
                sb.append(s.toLowerCase());
                sb.append(" ");
            }
            in.close();
            String[] words = sb.toString().replaceAll("[\\W&&[^\\s]]\\w*", " ").split("\\s+");//to get individual terms
            String[] tmpWords = new String[words.length];
            if (DocumentParser.enableWordStem) {
                for (int i = 0; i < words.length; i++) {
                    tmpWords[i] = words[i].replace(words[i], WordStemming.implementStem(words[i]));
                }
                words = tmpWords;
            }           
            this.wordCount = words.length;           
            initHashMap(words);
        }
    }

    public String getCosineMaps() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> entry : cosineMaps.entrySet()) {
            if(entry.getValue()!=0.000000){
                sb.append(String.format("%45s %-30s %10.6f %n", " ",entry.getKey(), entry.getValue()));            
            }           
        }
        return sb.toString();
    }

    public void printWordMaps() {
        System.out.println(fileName + ": " + wordCount);
        for (Map.Entry<String, Integer> entry : wordMaps.entrySet()) {
            System.out.print("\t");
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    private void initHashMap(String[] words) {
        String[] cmpWords = words.clone();
        for (String word : words) {
            int count = 0;
            if (DocumentParser.enableStopWord) {
                if (!wordMaps.containsKey(word) && !StopWord.hs.contains(word)) {
                    for (int i = 0; i < cmpWords.length; i++) {
                        if (word.equalsIgnoreCase(cmpWords[i])) {
                            count++;
                            cmpWords[i] = null;
                        }
                    }
                    wordMaps.put(word, count);
                }
            }
            else if (!wordMaps.containsKey(word)) {
                for (int i = 0; i < cmpWords.length; i++) {
                    if (word.equalsIgnoreCase(cmpWords[i])) {
                        count++;
                        cmpWords[i] = null;
                    }
                }
                wordMaps.put(word, count);
            }
        }
    }

    public void sortCosineMap() {
        List<Map.Entry<String, Double>> entries = new LinkedList<>(cosineMaps.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(entries);//descending order
        HashMap<String, Double> sortedMap = new LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Double> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
            count++;
            if (count > 2) {
                break;
            }
        }
        cosineMaps = sortedMap;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWordCount() {
        return wordCount;
    }

    public HashMap<String, Integer> getWordMaps() {
        return wordMaps;
    }

    public void setCosineMaps(HashMap<String, Double> cosineMaps) {
        this.cosineMaps = cosineMaps;
    }
}
