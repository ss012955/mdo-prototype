package HelperClasses;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.TextView;

import com.example.prototype.R;
import com.example.prototype.MainActivity;

public class DashboardManager {
    private BaseClass baseClass = new BaseClass();
    // Show the logout confirmation dialog

    public DashboardManager() {
        this.baseClass = baseClass;
    }

    public DashboardManager(BaseClass baseClass) {
        this.baseClass = baseClass;
    }

    public void showLogoutValidator(Context context, final Activity activity) {

        baseClass.showTwoButtonDialog(context, "Logout", "Are you sure you want to log out?", "LOGOUT", "Cancel",v->{            SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("is_logged_in", false).apply();

            // Start MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            activity.finish(); // Close current activity


            }, v->{});

    }
}
