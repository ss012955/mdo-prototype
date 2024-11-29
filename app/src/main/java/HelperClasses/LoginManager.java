package HelperClasses;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.prototype.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.NetworkUtils;

public class LoginManager implements DefaultLifecycleObserver {
    private final ExecutorService executorService;
    private final Context context;
    private final FirebaseAuth mAuth;


    public LoginManager(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.mAuth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth

    }
    public void performLogin(String umakEmail, String password, LoginCallback callback) {
        executorService.execute(() -> {
            // Perform login through Firebase Authentication
            mAuth.signInWithEmailAndPassword(umakEmail, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // Email is verified, allow login
                                ((MainActivity) context).runOnUiThread(() -> {
                                    callback.onLoginSuccess();
                                });

                                user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        String token = tokenTask.getResult().getToken();

                                        // Save token to SharedPreferences
                                        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString("id_token", token);
                                        editor.apply();

                                        Log.d("LoginManager", "Token saved successfully: " + token);

                                        // Notify login success
                                        ((MainActivity) context).runOnUiThread(callback::onLoginSuccess);
                                    } else {
                                        Log.e("LoginManager", "Error fetching token: " + tokenTask.getException());
                                        ((MainActivity) context).runOnUiThread(() -> callback.onLoginFailed("Failed to get ID token."));
                                    }
                                });

                            } else {
                                // Email not verified
                                ((MainActivity) context).runOnUiThread(() -> {
                                    callback.onLoginFailed("Email not verified. Check your email.");
                                });
                            }
                        } else {
                            ((MainActivity) context).runOnUiThread(() -> {
                                callback.onLoginFailed("Incorrect username or password.");
                            });
                        }
                    });
        });
    }

    // Callback interface
    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailed(String message);
    }


}
