package fr.iutlyon1.androidvelov.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Collection;

public final class LatLngUtils {

    private LatLngUtils() {}

    public static LatLngBounds computeBounds(Collection<? extends ClusterItem> items) {
        if (items.isEmpty()) {
            return null;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (ClusterItem item : items) {
            builder.include(item.getPosition());
        }

        return builder.build();
    }

    public static double computeDistance(LatLng from, LatLng to) {
        double lat = to.latitude - from.latitude;
        double lng = to.longitude - from.longitude;

        return Math.sqrt(Math.pow(lat, 2) + Math.pow(lng, 2));
    }
}
