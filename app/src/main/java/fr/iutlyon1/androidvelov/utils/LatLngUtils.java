package fr.iutlyon1.androidvelov.utils;

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
}
