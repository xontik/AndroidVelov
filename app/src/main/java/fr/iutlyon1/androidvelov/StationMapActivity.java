package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.display.VelovClusterRenderer;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.InternetUtils;
import fr.iutlyon1.androidvelov.utils.LatLngUtils;
import fr.iutlyon1.androidvelov.utils.MapUtils;
import fr.iutlyon1.androidvelov.utils.TextWatcherAdapter;
import fr.iutlyon1.androidvelov.utils.ViewUtils;

public class StationMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "StationMapActivity";
    private static final String SAVE_FILE = "velocData.ser";

    private GoogleMap map = null;
    private ClusterManager<VelovStationData> clusterManager = null;

    private AutoCompleteTextView search;
    private ImageButton searchEmpty;

    private VelovData velovData;

    public StationMapActivity() {
        velovData = new VelovData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize search bar
        search = findViewById(R.id.search);
        searchEmpty = findViewById(R.id.search_empty);

        search.setOnFocusChangeListener((view, hasFocus) -> {
            if (map == null) {
                return;
            }

            if (hasFocus && !velovData.isEmpty()) {
                MapUtils.setBounds(map, velovData.getStations());
            }
        });
        search.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();

                ViewUtils.animateVisibility(searchEmpty, !searchString.isEmpty());
                if (clusterManager == null)
                    return;

                List<VelovStationData> filteredStationList = velovData.find(searchString);
                clusterManager.clearItems();
                clusterManager.addItems(filteredStationList);

                if (filteredStationList.size() != 0) {
                    LatLngBounds bounds = LatLngUtils.computeBounds(filteredStationList);
                    map.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, MapUtils.MAP_PADDING));
                }
            }
        });

        searchEmpty.setOnClickListener(view -> search.setText(""));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (map != null) {
            map.setOnMapLoadedCallback(this::loadData);
        }
    }

    @Override
    protected void onStop() {
        OutputStream out;
        ObjectOutputStream oos;

        try {
            out = openFileOutput(SAVE_FILE, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(out);

            oos.writeObject(velovData);

            oos.close();
        } catch (IOException e) {
            Log.e(TAG, "onStop: " + e.getMessage(), e);
        }

        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        initClusterManager();

        velovData.addOnFirstLoadListener((velovData) -> {
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

    private void loadData() {
        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");

        if (!InternetUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.toast_loadSavedData , Toast.LENGTH_SHORT)
                    .show();
            loadLastSave();
        } else {
            final VelovRequest request = new VelovRequest(this, "Lyon", apiKey);
            request.execute(velovData);
        }
    }

    private void loadLastSave() {
        InputStream in;
        ObjectInputStream ois;
        VelovData savedData = null;

        try {
            in = openFileInput(SAVE_FILE);
            ois = new ObjectInputStream(in);

            savedData = (VelovData) ois.readObject();

            ois.close();
        } catch (IOException|ClassNotFoundException e) {
            Log.e(TAG, "loadLastSave: " + e.getMessage(), e);
        }

        if (savedData != null) {
            this.velovData.setAll(savedData.getStations());
        }
    }

    private VelovStationData selectedStation = null;

    private void initClusterManager() {
        clusterManager = new ClusterManager<>(this, map);

        clusterManager.setRenderer(new VelovClusterRenderer(this, map, clusterManager));
        clusterManager.setOnClusterItemClickListener(station -> {
            selectedStation = station;
            return false;
        });
        clusterManager.setOnClusterClickListener(cluster -> {
            selectedStation = null;
            return false;
        });

        clusterManager.setOnClusterItemInfoWindowClickListener(velovStationData -> {
            Intent intent = new Intent(StationMapActivity.this, StationDetailActivity.class);
            intent.putExtra("station", velovStationData);

            startActivity(intent);
        });

        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new VelovInfoWindow());

        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
    }

    private void updateGoogleMap() {
        if (map != null && clusterManager != null && !velovData.isEmpty()) {
            final List<VelovStationData> stations = velovData.getStations();

            clusterManager.clearItems();
            clusterManager.addItems(stations);
        }
    }

    private void updateSearchBar() {
        String[] names = new String[velovData.size()];
        for (int i = 0, length = velovData.size(); i < length; i++) {
            names[i] = velovData.get(i).getFullName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                names);
        search.setAdapter(adapter);
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

            view.setBackground(getDrawable(R.drawable.window_info_background));

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
}
