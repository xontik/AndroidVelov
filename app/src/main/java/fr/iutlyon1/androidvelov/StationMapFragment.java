package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.Locale;

import fr.iutlyon1.androidvelov.display.VelovClusterRenderer;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.AnimUtils;
import fr.iutlyon1.androidvelov.utils.PermissionUtils;
import fr.iutlyon1.androidvelov.utils.Property;

public class StationMapFragment extends Fragment implements OnMapReadyCallback {
    public interface OnMapFragmentInteractionListener {
        void onInfoWindowClick(VelovStationData station);
    }

    private GoogleMap mMap = null;
    private ClusterManager<VelovStationData> mClusterManager = null;
    private FloatingActionButton mGotoStation;

    private OnMapFragmentInteractionListener mListener;
    private VelovData mDataset;
    private Property<VelovStationData> selectedStation;

    public StationMapFragment() {
        selectedStation = new Property<>();
    }

    public static StationMapFragment newInstance(VelovData dataset) {
        StationMapFragment fragment = new StationMapFragment();
        Bundle args = new Bundle();
        args.putSerializable("dataset", dataset);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("A dataset is needed");
        }

        mDataset = (VelovData) args.getSerializable("dataset");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize goto fab
        mGotoStation = view.findViewById(R.id.goto_station);
        mGotoStation.setOnClickListener(v -> {
            if (selectedStation.isNull())
                return;

            final String uriFormat = "google.navigation:q=%f,%f&mode=b";
            LatLng position = selectedStation.get().getPosition();

            Uri uri = Uri.parse(String.format(Locale.ENGLISH, uriFormat, position.latitude, position.longitude));

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);
        });

        // Listners
        selectedStation.addOnChangeListener((old, nu) -> {
            if (nu != null)
                AnimUtils.slideIn(mGotoStation);
            else
                AnimUtils.slideOut(mGotoStation);
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMapFragmentInteractionListener) {
            mListener = (OnMapFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

        initGoogleMap();
        initClusterManager();
    }

    private void initGoogleMap() {
        mMap.getUiSettings().setMapToolbarEnabled(false);

        if (PermissionUtils.checkLocationPermission(getActivity())) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(latLng -> selectedStation.set(null));
        mDataset.addOnFilterUpdateListener((filter, filteredStations, dataset) ->
                updateGoogleMap(filteredStations));
    }

    private void initClusterManager() {
        Context context = getContext();
        mClusterManager = new ClusterManager<>(context, mMap);

        mClusterManager.setRenderer(new VelovClusterRenderer(context, mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(station -> {
            selectedStation.set(station);
            return false;
        });
        mClusterManager.setOnClusterClickListener(cluster -> {
            selectedStation.set(null);
            return false;
        });

        mClusterManager.setOnClusterItemInfoWindowClickListener(velovStationData -> {
            if (!selectedStation.isNull() && mListener != null) {
                mListener.onInfoWindowClick(selectedStation.get());
            }
        });

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new VelovInfoWindow());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
    }

    private final static float DEFAULT_ZOOM = 16.5f;

    public void centerOn(@NonNull Location location) {
        if (mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }
    }

    private void updateGoogleMap(List<VelovStationData> stations) {
        if (mMap != null && mClusterManager != null) {
            mClusterManager.clearItems();
            mClusterManager.addItems(stations);
            mClusterManager.cluster();

            selectedStation.set(stations.size() == 1 ?
                stations.get(0)
                : null);
        }
    }

    private class VelovInfoWindow implements GoogleMap.InfoWindowAdapter {
        private final View view;

        VelovInfoWindow() {
            view = getLayoutInflater().inflate(R.layout.list_item, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            final VelovStationData station = selectedStation.get();
            if (station == null)
                return null;

            view.setBackground(getContext().getDrawable(R.drawable.window_info_background));

            final ImageView favorite = view.findViewById(R.id.favoriteIcon);
            final TextView name = view.findViewById(R.id.stationName);
            final ImageView bikeIcon = view.findViewById(R.id.bikeIcon);
            final TextView availableBikes = view.findViewById(R.id.stationAvailableBikes);
            final ImageView standIcon = view.findViewById(R.id.standIcon);
            final TextView availableStands = view.findViewById(R.id.stationAvailableBikeStands);

            favorite.setImageResource(station.isFavorite()
                ? R.drawable.ic_favorite_black_24dp
                : R.drawable.ic_favorite_border_black_24dp);
            name.setText(station.getFullName());

            bikeIcon.setImageResource(R.drawable.ic_directions_bike_black_24dp);
            availableBikes.setText(String.valueOf(station.getAvailableBikes()));

            standIcon.setImageResource(R.drawable.ic_local_parking_black_24dp);
            availableStands.setText(String.valueOf(station.getAvailableBikeStands()));

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
