package calculation;

import com.google.gson.*;
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

    public static List<String> SearchTermExpansion(String input) throws MalformedURLException, UnsupportedEncodingException, IOException {
        StringBuilder sb = new StringBuilder();
        query = input;
        searchTerm = new ArrayList<>();
        for (int i = 0; i < 12; i = i + 4) {
            String address = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start=" + i + "&q=";
            String charset = "UTF-8";
            GoogleResults results;
            try {
                URL url = new URL(address + URLEncoder.encode(query, charset));
                System.out.println(url);
                Reader reader = new InputStreamReader(url.openStream(), charset);
                results = new Gson().fromJson(reader, GoogleResults.class);
            } catch (Exception e) {
                System.out.println("[-]Failed to expand  terms search");
                break;
            }
            System.out.println("[+] Processing " + (i + 1) + " ~ " + (i + 4) + " Google Search Results");

            for (int j = 0; j <= 3; j++) {
                try {
                    System.out.println(results.getResponseData().getResults().get(j).getUrl());
                    org.jsoup.nodes.Document doc = Jsoup.connect(results.getResponseData().getResults().get(j).getUrl()).get();
                    sb.append(doc.select("p").text().toLowerCase());
                    //  newsHeadlines = "Hi're] YOU "; test text for regex
                    System.out.println("[+] Proccessed " + (i + j + 1) + " Google Search Result");
                    words = sb.toString().replaceAll("[\\W&&[^\\s]]\\w*", " ").split("\\W+");
                } catch (Exception e) {
                    System.out.println("[-] Failed Processing " + (i + j + 1) + "result : " + e);
                }
            }
            System.out.println("[+] Processed " + (i+1) + " ~ " + (i + 4) + " Google Search Results");
        }
        HashMap<String, Integer> hm = new HashMap();

        for (String s : words) {
            int count = 1;
            if (StopWord.hs.contains(s)) {
                continue;
            }
            if (hm.containsKey(s)) {
                count += (int) hm.get(s);
            }
            hm.put(s, count);
        }

        LinkedHashMap<String, Integer> sortedMap = Sort(hm);
        String[] queryWords = query.toLowerCase().split(" ");
        for (String s : queryWords) {
            sortedMap.remove(s);
            searchTerm.add(s);
        }
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            System.out.println("Term added thru Expansion : " + entry.getKey());
            searchTerm.add(entry.getKey());
            count++;
            if (count == 5) {
                break;
            }
        }
        return searchTerm;
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
