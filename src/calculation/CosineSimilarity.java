/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

/**
 *
 * @author Daniel
 */
public class CosineSimilarity {

    public double getCosineSimilarity(Double[] tfidfVector1, Double[] tfidfVector2) {
        double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (int i = 0; i < tfidfVector1.length; i++) {
            dotProduct += (tfidfVector1[i] * tfidfVector2[i]);
            magnitude1 += Math.pow(tfidfVector1[i], 2);
            magnitude2 += Math.pow(tfidfVector2[i], 2);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 != 0.0 && magnitude2 != 0.0) {
            return dotProduct / (magnitude1 * magnitude2);
        } else {
            return 0.0;
        }        
    }

}
