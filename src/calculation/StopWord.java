/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package calculation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author Zheng Wei
 */
public class StopWord {
    
    public static String fileName;
    public static HashSet hs = new HashSet();
    public String[] stopWords;
    
    public StopWord(String cvsFilename){
        this.fileName=cvsFilename;       
    }
    
    public void initStopWords() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!= null){
            sb.append(line);
        }
        this.stopWords = sb.toString().split(",");
        for(String s:stopWords){
            hs.add(s);
        }
    }
}
    
