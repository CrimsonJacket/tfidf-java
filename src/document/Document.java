package document;


import calculation.StopWord;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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
public class Document{

    private String fileName;
    private HashMap<String, Integer> wordMaps = new HashMap<>();
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
            String[] words = sb.toString().replaceAll("[[`~!@#$%^&*()_+-={}|\\:\";<>,.?\\/\\[\\]]&&[^\\\\s]]", " ").split("\\s+");//to get individual terms
            this.wordCount = words.length;
            initHashMap(words);
        }
    }
    
    public void printWordMaps(){
        System.out.println(fileName + ": " + wordCount);
        for(Map.Entry<String, Integer> entry : wordMaps.entrySet()){
            System.out.print("\t");
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }

    private void initHashMap(String[] words) {
        String[] cmpWords = words.clone();
        for (String word : words) {
            int count = 0;
            if (!wordMaps.containsKey(word) && !StopWord.hs.contains(word)) {
                for (int i = 0; i < cmpWords.length; i++) {
                    if (word.equalsIgnoreCase(cmpWords[i])) {
                        count++;
                        cmpWords[i]=null;
                    }
                }     
                wordMaps.put(word, count);                
            }
        }
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

}