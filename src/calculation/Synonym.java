/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

/**
 *
 * @author JL
 */
import main.TfIdf_Frame;
import rita.wordnet.*;

public class Synonym {

    private static int numOfSearch = 3;

    public static String getSynonym(String userInput) {
        TfIdf_Frame.appendMessage("[+] Started Synonym Sequence");
        StringBuilder sb = new StringBuilder();
        StringBuilder termsAdded = new StringBuilder();
        RiWordnet wordnet = new RiWordnet(null);
        String[] tmpTerms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
        TfIdf_Frame.appendMessage("    Concatenating new Terms.");
        for (String tmp : tmpTerms) {
            String[] synonyms = wordnet.getSynset(tmp, "n");
            sb.append(tmp).append(" ");
            if (synonyms != null) {               
                for (String syn : synonyms) {
                    termsAdded.append(syn).append(" ");
                    sb.append(syn).append(" ");              
                }
            }
        }
        TfIdf_Frame.appendMessage("[+] Done: Synonym.");
        TfIdf_Frame.appendMessage("    Term(s) added: " + termsAdded.toString());
        return sb.toString();
    }

    public static String getHyponym(String userInput) {
        TfIdf_Frame.appendMessage("[+] Started Hyponym Sequence");
        StringBuilder sb = new StringBuilder();
        RiWordnet wordnet = new RiWordnet(null);
        StringBuilder termsAdded = new StringBuilder();
        String[] tmpTerms = userInput.replaceAll("[\\W&&[^\\s]]", " ").split("\\W+");
        TfIdf_Frame.appendMessage("    Concatenating new Terms.");
        for (String tmp : tmpTerms) {
            String[] hyponym = wordnet.getHyponyms(tmp, "n");
            if (hyponym != null) {
                for (String syn : hyponym) {
                    termsAdded.append(syn).append(" ");
                    sb.append(syn).append(" ");
                }               
            }
        }
        TfIdf_Frame.appendMessage("[+] Done: Hyponym.");
        TfIdf_Frame.appendMessage("    Term(s) added: " + termsAdded.toString());
        return sb.toString();
    }

}