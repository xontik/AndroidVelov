package fr.iutlyon1.androidvelov.display;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import fr.iutlyon1.androidvelov.model.VelovStationData;

public class VelovClusterRenderer extends DefaultClusterRenderer<VelovStationData> {

    public VelovClusterRenderer(Context context, GoogleMap map, ClusterManager<VelovStationData> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(VelovStationData item, MarkerOptions markerOptions) {


        if (item.getAvailableBikes() > 0 && item.getAvailableBikeStands() > 0) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (item.getAvailableBikeStands() == 0) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        super.onBeforeClusterItemRendered(item, markerOptions);
    }
}
