package fr.iutlyon1.androidvelov;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationListActivity extends AppCompatActivity {

    private ListView stationListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        stationListView = findViewById(R.id.stationListView);

        VelovData items =  new VelovData();
        //    public VelovStationData(int number, String name, String address, Position position, boolean banking, boolean bonus, String status, String contractName, int bikeStands, int availableBikeStands, int availableBikes, long lastUpdate) {

        items.add(new VelovStationData(
                1,
                "Station name",
                "18 rue léon Fabre",
                new VelovStationData.Position(10,10),
                true,
                false,
                "OPEN",
                "Lyon",
                20,
                5,
                15,
                0 )
        );

        StationListAdapter adapter = new StationListAdapter (this.getBaseContext(), items);

        stationListView.setAdapter(adapter);
    }
}
