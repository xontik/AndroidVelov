package fr.iutlyon1.androidvelov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Properties;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationListActivity extends AppCompatActivity {

    private ListView stationListView;
    private Properties properties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        loadProperties();

        stationListView = findViewById(R.id.stationListView);


        StationListAdapter adapter = new StationListAdapter(this.getBaseContext(), new VelovData());
        stationListView.setAdapter(adapter);

        loadVelovData(adapter);

        stationListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent();
            intent.putExtra("station", (VelovStationData) adapter.getItem(position));
            startActivity(intent);
        });
    }

    private void loadProperties() {
        try {
            final InputStream propertiesStream = getAssets().open("app.properties");
            properties = new Properties();
            properties.load(propertiesStream);

            Log.d("StationListActivity", "apiKey = " + properties.getProperty("API_KEY"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("StationListActivity", e.getMessage());
        }
    }

    private void loadVelovData(StationListAdapter adapter) {
        final String apiKey = properties.getProperty("API_KEY");

        VelovRequest velovRequest = new VelovRequest("Lyon", apiKey);
        velovRequest.execute(adapter);
    }
}
