/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

/**
 *
 * @author JL
 */
import rita.wordnet.*;

public class Synonym {

    private static int numOfSearch = 3;

    public static String getSynonym(String userInput) {
        StringBuilder sb = new StringBuilder();
        RiWordnet wordnet = new RiWordnet(null);
        String[] tmpTerms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
        for (String tmp : tmpTerms) {
            String[] synonyms = wordnet.getSynonyms(tmp, "n", numOfSearch);
            sb.append(tmp);
            sb.append("\n");
            if (synonyms != null) {
                for (String syn : synonyms) {
                    sb.append(syn);
                    sb.append("\n");
                }

            }
        }
        return sb.toString();
    }

    public static String getHyponym(String userInput) {
        StringBuilder sb = new StringBuilder();
        RiWordnet wordnet = new RiWordnet(null);
        String[] tmpTerms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
        for (String tmp : tmpTerms) {
            String[] hyponym = wordnet.getHyponyms(tmp, "n");
            sb.append(tmp);
            sb.append("\n");
            if (hyponym != null) {
                for (String syn : hyponym) {
                    sb.append(syn);
                    sb.append("\n");
                }
            }

        }
        return sb.toString();
    }

}