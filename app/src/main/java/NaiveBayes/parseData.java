package NaiveBayes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class parseData {
    private static final String TAG = "ParseData";
    private Map<String, List<String>> trainingData;

    public parseData(Context context) {
        trainingData = new HashMap<>();

        try {
            // Open the JSON file from assets
            InputStream is = context.getAssets().open("disease_symptoms.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert byte array to String
            String json = new String(buffer, "UTF-8");

            // Parse the JSON String to JSONArray
            JSONArray jsonArray = new JSONArray(json);

            // Iterate over each entry in the JSON array
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String text = item.getString("text");
                int label = item.getInt("label");

                // Create a list to hold symptoms
                List<String> symptoms = new ArrayList<>();

                // Here you can split the text if it contains multiple symptoms, for example:
                String[] symptomsArray = text.split(","); // Assuming symptoms are comma-separated

                // Add symptoms to the list
                for (String symptom : symptomsArray) {
                    symptoms.add(symptom.trim());
                }

                // Put the label as key and symptoms list as value in the training data
                trainingData.put(String.valueOf(label), symptoms);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading or parsing the JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getTrainingData() {
        return trainingData;
    }
}
