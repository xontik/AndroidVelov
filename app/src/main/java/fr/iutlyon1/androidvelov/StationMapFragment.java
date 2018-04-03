package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import fr.iutlyon1.androidvelov.display.VelovClusterRenderer;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.LatLngUtils;
import fr.iutlyon1.androidvelov.utils.MapUtils;
import fr.iutlyon1.androidvelov.utils.TextWatcherAdapter;
import fr.iutlyon1.androidvelov.utils.ViewUtils;

public class StationMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "StationMapFragment";

    private GoogleMap mMap = null;
    private AutoCompleteTextView mSearch;
    private ImageButton mSearchEmpty;

    private ClusterManager<VelovStationData> mClusterManager = null;

    private OnMapFragmentInteractionListener mListener;
    private VelovData mDataset;

    public StationMapFragment() {
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

        // Initialize search bar
        mSearch = view.findViewById(R.id.search);
        mSearchEmpty = view.findViewById(R.id.search_empty);

        mSearch.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();

                ViewUtils.animateVisibility(mSearchEmpty, !searchString.isEmpty());
                if (mClusterManager == null)
                    return;

                List<VelovStationData> filteredStationList = mDataset.find(searchString);
                mClusterManager.clearItems();
                mClusterManager.addItems(filteredStationList);

                if (filteredStationList.size() != 0) {
                    LatLngBounds bounds = LatLngUtils.computeBounds(filteredStationList);
                    mMap.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, MapUtils.MAP_PADDING));
                }
            }
        });

        mSearchEmpty.setOnClickListener(v -> mSearch.setText(""));

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

        initClusterManager();

        mDataset.addOnFirstLoadListener((velovData) -> {
            if (!velovData.isEmpty()) {
                LatLngBounds bounds = LatLngUtils.computeBounds(velovData.getStations());
                map.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, MapUtils.MAP_PADDING));
            }

            velovData.addOnItemsUpdateListener(d -> {
                updateGoogleMap();
                updateSearchBar();
            });
        });
    }

    private VelovStationData selectedStation = null;

    private void initClusterManager() {
        Context context = getContext();
        mClusterManager = new ClusterManager<>(context, mMap);

        mClusterManager.setRenderer(new VelovClusterRenderer(context, mMap, mClusterManager));
        mClusterManager.setOnClusterItemClickListener(station -> {
            selectedStation = station;
            return false;
        });
        mClusterManager.setOnClusterClickListener(cluster -> {
            selectedStation = null;
            return false;
        });

        mClusterManager.setOnClusterItemInfoWindowClickListener(velovStationData -> {
            if (selectedStation != null && mListener != null) {
                mListener.onInfoWindowClick(selectedStation);
            }
        });

        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new VelovInfoWindow());

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
    }

    private void updateGoogleMap() {
        if (mMap != null && mClusterManager != null && !mDataset.isEmpty()) {
            mClusterManager.clearItems();
            mClusterManager.addItems(mDataset.getStations());
        }
    }

    private void updateSearchBar() {
        String[] names = new String[mDataset.size()];
        for (int i = 0, length = mDataset.size(); i < length; i++) {
            names[i] = mDataset.get(i).getFullName();
        }

        Context context = getContext();
        if (context != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_list_item_1,
                    names);
            mSearch.setAdapter(adapter);
        }
    }

    private class VelovInfoWindow implements GoogleMap.InfoWindowAdapter {
        private final View view;

        VelovInfoWindow() {
            view = getLayoutInflater().inflate(R.layout.list_item, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (selectedStation == null)
                return null;

            view.setBackground(getContext().getDrawable(R.drawable.window_info_background));

            final ImageView favorite = view.findViewById(R.id.favoriteIcon);
            final TextView name = view.findViewById(R.id.stationName);
            final ImageView bikeIcon = view.findViewById(R.id.bikeIcon);
            final TextView availableBikes = view.findViewById(R.id.stationAvailableBikes);
            final ImageView standIcon = view.findViewById(R.id.standIcon);
            final TextView availableStands = view.findViewById(R.id.stationAvailableBikeStands);

            favorite.setImageResource(selectedStation.isFavorite()
                ? R.drawable.ic_favorite_black_24dp
                : R.drawable.ic_favorite_border_black_24dp);
            name.setText(selectedStation.getFullName());

            bikeIcon.setImageResource(R.drawable.ic_directions_bike_black_24dp);
            availableBikes.setText(String.valueOf(selectedStation.getAvailableBikes()));

            standIcon.setImageResource(R.drawable.ic_local_parking_black_24dp);
            availableStands.setText(String.valueOf(selectedStation.getAvailableBikeStands()));

            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    public interface OnMapFragmentInteractionListener {
        void onInfoWindowClick(VelovStationData station);
    }
}
