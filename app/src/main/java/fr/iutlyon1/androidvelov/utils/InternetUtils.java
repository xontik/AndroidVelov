package fr.iutlyon1.androidvelov.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import fr.iutlyon1.androidvelov.R;

public final class InternetUtils {
    private InternetUtils() { }

    /**
     * Checks if the device is connected to a network or not.
     *
     * @param context The context
     * @return Whether a network is accessible or not
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            throw new RuntimeException("Can't access Connectivity Service");
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    public static void showConnectionDialog(Context context) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);

        adb.setTitle(R.string.dialog_noConnection_title);
        adb.setMessage(R.string.dialog_noConnection_content);

        adb.setPositiveButton(R.string.dialog_ok, null);

        adb.show();
    }
}
