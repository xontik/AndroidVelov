package fr.iutlyon1.androidvelov;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.display.VelovClusterRenderer;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.LatLngUtils;
import fr.iutlyon1.androidvelov.utils.MapUtils;
import fr.iutlyon1.androidvelov.utils.TextWatcherAdapter;
import fr.iutlyon1.androidvelov.utils.ViewUtils;

public class StationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map = null;
    private ClusterManager<VelovStationData> clusterManager = null;

    private AutoCompleteTextView search;
    private ImageButton searchEmpty;

    private VelovData velovData;

    StationMapActivity() {
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

                List<VelovStationData> filteredStationList = velovData.find(searchString);
                clusterManager.clearItems();
                clusterManager.addItems(filteredStationList);

                LatLngBounds bounds = LatLngUtils.computeBounds(filteredStationList);
                map.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(bounds, MapUtils.MAP_PADDING));
            }
        });

        searchEmpty.setOnClickListener(view -> search.setText(""));

        velovData.addOnItemsUpdateListener(data -> {
            updateGoogleMap();
            updateSearchBar();
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        initClusterManager();
        fetchData();
    }

    private void initClusterManager() {
        clusterManager = new ClusterManager<>(this, map);

        clusterManager.setRenderer(new VelovClusterRenderer(this, map, clusterManager));

        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
    }

    private void fetchData() {
        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");

        final VelovRequest request = new VelovRequest(this, "Lyon", apiKey);
        request.execute(velovData);
    }

    private void updateGoogleMap() {
        if (clusterManager != null) {
            List<VelovStationData> stations = velovData.getStations();

            clusterManager.clearItems();
            clusterManager.addItems(stations);

            LatLngBounds bounds = LatLngUtils.computeBounds(stations);
            map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(bounds, MapUtils.MAP_PADDING));
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
}
