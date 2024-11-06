package HelperClasses;
import android.content.Context;
import android.widget.Toast;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.prototype.MainActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Database.NetworkUtils;

public class LoginManager implements DefaultLifecycleObserver {
    private final ExecutorService executorService;
    private final Context context;

    public LoginManager(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }
    public void performLogin(String studentId, String password, LoginCallback callback) {
        executorService.execute(() -> {
            String result = NetworkUtils.performLogin(studentId, password);

            // Use runOnUiThread to update UI elements
            ((MainActivity) context).runOnUiThread(() -> {
                if (result.equals("Login successful!")) {
                    callback.onLoginSuccess();
                } else {
                    callback.onLoginFailed();
                }
            });
        });
    }

    // Callback interface
    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailed();
    }


}
