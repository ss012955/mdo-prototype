package com.example.prototype;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import HelperClasses.ApiService;
import NaiveBayes.NaiveBayesClassifier;
import NaiveBayes.parseData;
import api.PredictionRequest;
import api.PredictionResponse;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NaiveBayes extends AppCompatActivity {

    private EditText symptomsInput;
    private TextView resultText;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naive_bayes);

        // Initialize views
        symptomsInput = findViewById(R.id.symptomsInput);
        Button predictButton = findViewById(R.id.predictButton);
        resultText = findViewById(R.id.resultText);

        // Setup Retrofit
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/") // Use this for Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);

        // Set click listener
        predictButton.setOnClickListener(v -> predictDisease());
    }

    private void predictDisease() {
        String symptoms = symptomsInput.getText().toString().trim();

        if (symptoms.isEmpty()) {
            Toast.makeText(this, "Please enter symptoms", Toast.LENGTH_SHORT).show();
            return;
        }

        PredictionRequest request = new PredictionRequest(symptoms);

        apiService.predictDisease(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse predictionResponse = response.body();
                    if ("success".equals(predictionResponse.getStatus())) {
                        String result = "Predicted Disease: " + predictionResponse.getPredictedDisease() +
                                "\nConfidence Score: " + predictionResponse.getConfidence_score();
                        resultText.setText(result);
                    } else {
                        resultText.setText("Error: " + predictionResponse.getMessage());
                    }
                } else {
                    resultText.setText("Error: Unable to get prediction");
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                resultText.setText("Error: " + t.getMessage());
            }
        });
    }
}