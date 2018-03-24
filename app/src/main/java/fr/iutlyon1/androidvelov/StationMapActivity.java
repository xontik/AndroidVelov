package fr.iutlyon1.androidvelov;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.MapUtils;
import fr.iutlyon1.androidvelov.utils.TextWatcherAdapter;
import fr.iutlyon1.androidvelov.utils.ViewUtils;

public class StationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map = null;
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

        search = findViewById(R.id.search);
        searchEmpty = findViewById(R.id.search_empty);

        search.setOnFocusChangeListener((view, hasFocus) -> {
            if (map == null) {
                return;
            }

            if (hasFocus && velovData.size() != 0) {
                MapUtils.setBounds(map, velovData);
            }
        });
        search.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String searchString = s.toString();

                ViewUtils.animateVisibility(searchEmpty, !searchString.isEmpty());

                List<VelovStationData> filteredStationList = velovData.find(searchString);
                MapUtils.setMarkers(map, filteredStationList);
            }
        });

        searchEmpty.setOnClickListener(view -> {
            search.setText("");
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");
        final VelovRequest request = new VelovRequest("Lyon", apiKey);
        request.execute(velovData, map, search);
    }
}
