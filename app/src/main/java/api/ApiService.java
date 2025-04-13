package HelperClasses;

import api.PredictionRequest;
import api.PredictionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("predict")
    Call<PredictionResponse> predictDisease(@Body PredictionRequest request);
}
