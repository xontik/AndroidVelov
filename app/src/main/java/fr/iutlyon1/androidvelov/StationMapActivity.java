package fr.iutlyon1.androidvelov;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import fr.iutlyon1.androidvelov.api.VelovRequest;

public class StationMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private AutoCompleteTextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search = findViewById(R.id.search);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");
        final VelovRequest request = new VelovRequest("Lyon", apiKey);
        request.execute(map, search);
    }
}
