package fr.iutlyon1.androidvelov.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import fr.iutlyon1.androidvelov.R;

public class PermissionUtils {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private PermissionUtils() {
    }

    public static boolean checkLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )) {
                // Show an explanation to the user asynchronously. After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(activity)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.dialog_ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                    activity,
                                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
