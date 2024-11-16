package HelperClasses;

import android.content.Context;
import android.widget.Toast;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.prototype.createAcc;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.NetworkUtils;

public class SignupManager implements DefaultLifecycleObserver {
    private static ExecutorService executorService = null;
    public static Context context = null;
    private static FirebaseAuth mAuth;

    public SignupManager(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    static {
        mAuth = FirebaseAuth.getInstance();
    }

    // Perform Signup with Firebase and XAMPP
    public static void performSignupWithFirebase(Context context, String studentId, String email, String firstName, String lastName, String password, final SignUpCallBack callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firebase user creation success
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Send email verification after signup
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            // Now insert the user data into MySQL with is_verified = false
                                            insertUserDataToDatabase(studentId, email, firstName, lastName, password, callback);
                                        } else {
                                            callback.onSignupFailed("Failed to send verification email.");
                                        }
                                    });
                        }
                    } else {
                        callback.onSignupFailed("Signup failed: " + task.getException().getMessage());
                    }
                });
    }


    public static void insertUserDataToDatabase(String studentId, String email, String firstName, String lastName, String password, SignUpCallBack callback) {
        executorService.execute(() -> {
            String result = NetworkUtils.performSignup(studentId, email, firstName, lastName, password);
            ((createAcc) context).runOnUiThread(() -> {
                if ("Signup successful!".equals(result)) {
                    callback.onSignupSuccess();
                } else if ("Email already registered".equals(result)) {
                    callback.onSignupFailed("Email already registered");
                }else if("Student ID already registered".equals(result)){
                    callback.onSignupFailed("Student ID already registered");
                } else {
                    callback.onSignupFailed("Registration Failed. Hosting problem.");
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Callback interface
    public interface SignUpCallBack {
        void onSignupSuccess();
        void onSignupFailed(String message);
    }
}
