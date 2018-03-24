package fr.iutlyon1.androidvelov.utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public final class MapUtils {
    public static final int MAP_PADDING = 48;

    private MapUtils() {}

    public static Marker addMarker(GoogleMap map, VelovStationData station) {
        MarkerOptions marker = new MarkerOptions()
                .position(station.getPosition())
                .title(station.getName());

        // Si aucun stand de vélo n'est disponible, on change la couleur
        if (station.getAvailableBikeStands() == 0) {
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }

        Marker mapMarker = map.addMarker(marker);
        mapMarker.setTag(station.getNumber());

        return mapMarker;
    }

    public static void setMarkers(GoogleMap map, Collection<? extends VelovStationData> stations) {
        final Set<Marker> markers = new HashSet<>(stations.size());

        map.clear();

        for (VelovStationData station : stations) {
            final Marker marker = addMarker(map, station);
            markers.add(marker);
        }

        LatLngBounds bounds = LatLngUtils.computeBounds(markers);
        map.animateCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING));
    }

    public static void setBounds(GoogleMap map, VelovData velovData) {
        final LatLngBounds bounds = LatLngUtils.computeBounds(velovData);

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING));
    }
}
