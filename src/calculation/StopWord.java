/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package calculation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 *
 * @author Zheng Wei
 */
public class StopWord {
    HashMap hm;
    int wordCount;
    public StopWord(HashMap hashMap, String CVSfilename,int wordCount) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(CVSfilename));
        String[] stopWords;
        String line;
        StringBuilder sb = new StringBuilder();
        this.wordCount = wordCount;
        while((line = br.readLine())!= null){
            sb.append(line);
        }
        stopWords = sb.toString().split(",");
        hm = hashMap;
        for (String s : stopWords){
            if(hm.containsKey(s)){
               // System.out.println("Line found! "+s);
               // System.out.println(hm.get(s));
                this.wordCount -= (int)hm.get(s);
                hm.remove(s);
            }
        }
    }

    /**
     * @return the hm
     */
    public HashMap getHm() {
        return hm;
    }

    /**
     * @return the wordCount
     */
    public int getWordCount() {
        return wordCount;
    }
    
}
    
