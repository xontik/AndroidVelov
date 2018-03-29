package fr.iutlyon1.androidvelov.utils;

import android.app.AlertDialog;
import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import fr.iutlyon1.androidvelov.R;

public final class InternetUtils {

    private InternetUtils() { }

    public static boolean isOnline() {
        return isOnline(2000);
    }

    public static boolean isOnline(int timeout) {
        try {
            Socket sock  = new Socket();
            SocketAddress addr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(addr, timeout);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void showConnectionDialog(Context context) {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);

        adb.setTitle(R.string.dialog_noConnection_title);
        adb.setMessage(R.string.dialog_noConnection_content);

        adb.setPositiveButton(R.string.dialog_ok, null);

        adb.show();
    }
}
