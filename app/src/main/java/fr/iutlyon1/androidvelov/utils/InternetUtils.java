package fr.iutlyon1.androidvelov.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import fr.iutlyon1.androidvelov.R;

public final class InternetUtils {

    private InternetUtils() { }

    /**
     * Beware ! Only use in async tasks !
     * See {@link #isNetworkAvailable(Context)} for use in main thread.
     *
     * @return Whether internet is accessible or not
     *
     * @see #isNetworkAvailable(Context)
     */
    public static boolean isOnline() {
        try {
            Socket sock = new Socket();
            SocketAddress addr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(addr, 1500);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

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
