package NaiveBayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NaiveBayesClassifier {
    private Map<String, Integer> diseaseCounts;  // Disease -> count
    private Map<String, Map<String, Integer>> symptomCounts;  // Disease -> (Symptom -> count)
    private int totalDiseases;

    public NaiveBayesClassifier(Map<String, List<String>> trainingData) {
        diseaseCounts = new HashMap<>();
        symptomCounts = new HashMap<>();
        train(trainingData);
    }

    // Train the Naive Bayes model on the provided training data
    private void train(Map<String, List<String>> data) {
        totalDiseases = data.size();

        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String disease = entry.getKey();
            List<String> symptoms = entry.getValue();

            // Count the disease
            diseaseCounts.put(disease, diseaseCounts.getOrDefault(disease, 0) + 1);

            // Count the symptoms for the given disease
            for (String symptom : symptoms) {
                symptomCounts.putIfAbsent(disease, new HashMap<>());
                Map<String, Integer> symptomFreq = symptomCounts.get(disease);
                symptomFreq.put(symptom, symptomFreq.getOrDefault(symptom, 0) + 1);
            }
        }
    }

    // Predict the disease for given symptoms
    public String predict(List<String> inputSymptoms) {
        String bestDisease = "";
        double maxProb = Double.NEGATIVE_INFINITY;

        // For each disease, calculate the log of the posterior probability
        for (String disease : diseaseCounts.keySet()) {
            double logProb = Math.log(diseaseCounts.get(disease) / (double) totalDiseases);

            for (String symptom : inputSymptoms) {
                int count = symptomCounts.get(disease).getOrDefault(symptom, 0);
                int total = symptomCounts.get(disease).size();
                double prob = (count + 1.0) / (total + 1.0); // Laplace Smoothing
                logProb += Math.log(prob);
            }

            if (logProb > maxProb) {
                maxProb = logProb;
                bestDisease = disease;
            }
        }

        return bestDisease;
    }
}

