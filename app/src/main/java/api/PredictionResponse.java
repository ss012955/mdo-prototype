package api;

public class PredictionResponse {
    private String status;
    private String predicted_disease;
    private int disease_index;
    private float confidence_score;
    private String message;

    public String getStatus() {
        return status;
    }

    public String getPredictedDisease() {
        return predicted_disease;
    }

    public int getDiseaseIndex() {
        return disease_index;
    }

    public String getMessage() {
        return message;
    }

    public float getConfidence_score(){
        return  confidence_score;
    }
}