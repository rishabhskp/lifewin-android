package app.lifewin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class is use to check the status of Internet connection that is it
 * available or not and return.
 *
 */
public class NetworkStatus {
    /**
     * this method check that is Internet connection available or not
     *
     * @return boolean
     */
    public static boolean isInternetOn(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}