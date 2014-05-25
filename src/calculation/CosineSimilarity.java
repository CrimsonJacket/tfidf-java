/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import document.DocumentParser;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Daniel
 */
public class CosineSimilarity {

    public Double calculateCosineSimilarity(HashMap<String, Double> entry, HashMap<String, Double> entryToCompare) {
		Double sum = 0.0;
		Double entryNorm = 0.0;
		Double compareNorm = 0.0;
		Set<String> terms = entry.keySet();
		Iterator<String> termIterator = terms.iterator();
		while (termIterator.hasNext()) {
			String term = termIterator.next();
			boolean containKey = entryToCompare.containsKey(term);
			if (containKey) {
                            sum += entry.get(term) * entryToCompare.get(term);
			}
		}
		entryNorm = calculateNorm(entry);
		compareNorm = calculateNorm(entryToCompare);
                
                if(entryNorm != 0.0 && compareNorm != 0.0){
                    return sum / (entryNorm * compareNorm);
                }else{
                    return 0.0;
                }
	}
    
    public Double calculateNorm(HashMap<String, Double> entry) {
		Double norm = 0.0;
		Set<String> terms = entry.keySet();
		Iterator<String> it = terms.iterator();
		while (it.hasNext()) {
			String term = it.next();
			norm += Math.pow(entry.get(term), 2);
		}
		return Math.sqrt(norm);
	}

}
