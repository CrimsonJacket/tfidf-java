package main;

import java.util.Map;

public class TfIdf {

    public double calculateTF(Map<String, Integer> map, String termsToCheck, int wordCount) {
        double count = 0;
        for(Map.Entry<String,Integer> entry: map.entrySet()){
            if(entry.getKey().equalsIgnoreCase(termsToCheck)){
                count += entry.getValue();
            }
        }
        return count/wordCount;
    }
    
    public double calculateIDF(String termToCheck){
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
        return Math.log10(numOfDocs/count);
    }

}
