package fr.iutlyon1.androidvelov.utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Collection;

import fr.iutlyon1.androidvelov.model.VelovStationData;

public final class MapUtils {
    public static final int MAP_PADDING = 48;

    private MapUtils() {}

    public static void setBounds(GoogleMap map, Collection<? extends VelovStationData> stations) {
        final LatLngBounds bounds = LatLngUtils.computeBounds(stations);

        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING));
    }
}
