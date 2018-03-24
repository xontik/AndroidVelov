package fr.iutlyon1.androidvelov.utils;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.Collection;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public final class LatLngUtils {

    private LatLngUtils() {}

    public static LatLngBounds computeBounds(Collection<? extends Marker> markers) {
        if (markers.size() == 0) {
            return null;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        return builder.build();
    }

    public static LatLngBounds computeBounds(VelovData velovData) {
        if (velovData.size() == 0) {
            return null;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (VelovStationData station : velovData) {
            builder.include(station.getPosition());
        }

        return builder.build();
    }
}
