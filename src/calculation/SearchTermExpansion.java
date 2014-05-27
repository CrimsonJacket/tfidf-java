package calculation;

import com.google.gson.*;
import document.DocumentParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import main.TfIdf_Frame;
import org.jsoup.Jsoup;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Zheng Wei
 */
public class SearchTermExpansion {

    private static List<String> searchTerm;
    private static String query;
    private static String[] words = null;

    public static String SearchTermExpansion(String input) throws MalformedURLException, UnsupportedEncodingException, IOException {
        StringBuilder sb = new StringBuilder();
        query = input;
        searchTerm = new ArrayList<>();
        for (int i = 0; i < 12; i = i + 4) {
            String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start=" + i + "&q=";
            String charset = "UTF-8";
            GoogleResults results;
            try {
                URL url = new URL(address + URLEncoder.encode(query, charset));
                Reader reader = new InputStreamReader(url.openStream(), charset);
                results = new Gson().fromJson(reader, GoogleResults.class);
            } catch (JsonIOException | JsonSyntaxException | IOException e) {  
                TfIdf_Frame.appendMessage("    Failed to expand term search");
                break;
            }
            TfIdf_Frame.appendMessage("    Processing " + (i+1) + " ~ " + (i + 4) + " Google Search Results");
            for (int j = 0; j <= 3; j++) {
                try {
                    org.jsoup.nodes.Document doc = Jsoup.connect(results.getResponseData().getResults().get(j).getUrl()).get();
                    sb.append(doc.select("p").text().toLowerCase());
                    words = sb.toString().replaceAll("[\\W&&[^\\s]]\\w*", " ").split("\\W+");
                    TfIdf_Frame.appendMessage("    Processed Google Search Result " +(i + j + 1));
                } catch (IOException e) {
                    TfIdf_Frame.appendMessage("    Failed to process Google result "+(i+j+1)+".");
                    TfIdf_Frame.appendMessage("    Please check your internet connection or try again later.");
                    
                }
            }
            
        }
        
        HashMap<String, Integer> hm = new HashMap();
        for (String s : words) {
            int count = 1;
            if (hm.containsKey(s)) {
                count += (int) hm.get(s);
            }
            hm.put(s, count);
        }

        HashMap<String, Integer> sortedMap = Sort(hm);
        sb = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            if (DocumentParser.enableStopWord) {
                if (!StopWord.hs.contains(entry.getKey())) {
                    if(!input.contains(entry.getKey())){
                        sb.append(entry.getKey());
                        sb.append(" ");
                        count++;
                    }                    
                }
            }else{
                sb.append(entry.getKey());
                sb.append(" ");
                count++;
            }
            if (count == 5) {
                break;
            }
        }
        TfIdf_Frame.appendMessage("    Term(s) added: " +sb.toString());
        return sb.toString();       
    }

    private static LinkedHashMap Sort(HashMap hashMap) {
        List<Map.Entry<String, Integer>> entries = new LinkedList<>(hashMap.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        Collections.reverse(entries);//descending order
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
