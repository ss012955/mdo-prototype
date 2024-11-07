package HelperClasses;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;

public class NetworkChangeReceiver extends BroadcastReceiver {

    // NetworkChangeListener interface
    public interface NetworkChangeListener {
        void onNetworkChange(boolean isConnected);
    }

    private NetworkChangeListener networkChangeListener;

    // Constructor to set listener
    public NetworkChangeReceiver(NetworkChangeListener listener) {
        this.networkChangeListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = isNetworkAvailable(context);
        if (networkChangeListener != null) {
            networkChangeListener.onNetworkChange(isConnected);
        }
    }

    // Helper function to check network availability
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true; // Wi-Fi is available
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // Check if mobile data has traffic
                long mobileDataUsage = TrafficStats.getMobileRxBytes() + TrafficStats.getMobileTxBytes();
                return mobileDataUsage > 0; // Only allow mobile data if there is traffic (active data)
            }
        }
        return false; // No valid connection
    }
}