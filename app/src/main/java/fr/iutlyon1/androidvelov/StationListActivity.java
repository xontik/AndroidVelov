package fr.iutlyon1.androidvelov;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationListActivity extends AppCompatActivity {

    private ListView stationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

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

    private void loadVelovData(StationListAdapter adapter) {
        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");

        VelovRequest velovRequest = new VelovRequest("Lyon", apiKey);
        velovRequest.execute(adapter);
    }
}
