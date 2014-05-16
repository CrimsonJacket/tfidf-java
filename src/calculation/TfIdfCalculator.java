package calculation;

import document.Document;
import document.DocumentParser;
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
public class TfIdfCalculator {

    public double calculateTF(HashMap<String, Integer> map, String termsToCheck, int wordCount) {
        double count = 0;
        for(Map.Entry<String,Integer> entry: map.entrySet()){
            if(entry.getKey().equalsIgnoreCase(termsToCheck)){
                count += entry.getValue();
            }
        }
        return count/wordCount;
    }
    
    public double calculateIDF(String termToCheck,double tf){
        double numOfDocs = DocumentParser.docArray.size();
        double count = 0;
        for(Document docs : DocumentParser.docArray){
            for(Map.Entry<String,Integer> entry : docs.getWordMaps().entrySet()){
                if(entry.getKey().equalsIgnoreCase(termToCheck)){
                    count++;
                    break;
                }
            }
        }
        if(tf==0.0){
            return Math.log10(numOfDocs/(count+1));
        }else{
            return Math.log10(numOfDocs/count);
        } 
    }

}
