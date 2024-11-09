package HelperClasses;

import android.content.Context;
import android.widget.Toast;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.prototype.createAcc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.NetworkUtils;

public class SignupManager implements DefaultLifecycleObserver {
    private final ExecutorService executorService;
    private final Context context;

    public SignupManager(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void performSignup(String studentId, String email, String firstname, String lastname, String password, SignUpCallBack callback) {
        executorService.execute(() -> {
            String result = NetworkUtils.performSignup(studentId, email, firstname, lastname, password);
            ((createAcc) context).runOnUiThread(() -> {
                if ("Signup successful!".equals(result)) {
                    callback.onSignupSuccess();
                } else {
                    callback.onSignupFailed();
                }
            });
        });
    }

    // Callback interface
    public interface SignUpCallBack {
        void onSignupSuccess();
        void onSignupFailed();
    }
}
