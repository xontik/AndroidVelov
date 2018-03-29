package fr.iutlyon1.androidvelov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.display.StationListAdapter;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationListActivity extends AppCompatActivity {

    private ListView stationListView;

    private VelovData velovData;

    StationListActivity() {
        velovData = new VelovData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        stationListView = findViewById(R.id.stationListView);

        StationListAdapter adapter = new StationListAdapter(this.getBaseContext(), new VelovData());
        stationListView.setAdapter(adapter);

        stationListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(StationListActivity.this, StationDetailActivity.class);
            intent.putExtra("station", (VelovStationData) adapter.getItem(position));
            startActivity(intent);
        });

        velovData.addOnItemsUpdateListener(adapter::setItems);

        loadVelovData();
    }

    private void loadVelovData() {
        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");

        VelovRequest velovRequest = new VelovRequest(this, "Lyon", apiKey);
        velovRequest.execute(velovData);
    }
}
