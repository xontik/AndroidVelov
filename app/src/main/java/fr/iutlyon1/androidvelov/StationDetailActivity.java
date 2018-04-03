package fr.iutlyon1.androidvelov;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;

import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        VelovStationData station = (VelovStationData) getIntent().getSerializableExtra("station");
        if (station == null)
            throw new IllegalArgumentException("Need a station !");

        show(station);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return true;
    }

    private void show(VelovStationData station) {
        final ImageView favorite = findViewById(R.id.favoriteIcon);
        favorite.setImageResource(station.isFavorite()
                ? R.drawable.ic_favorite_black_24dp
                : R.drawable.ic_favorite_border_black_24dp);

        final TextView fullname = findViewById(R.id.fullname);
        fullname.setText(station.getFullName());

        final TextView address = findViewById(R.id.address);
        address.setText(station.getAddress());

        final TextView latitude = findViewById(R.id.latitude);
        final TextView longitude = findViewById(R.id.longitude);
        final LatLng pos = station.getPosition();
        latitude.setText(String.valueOf(pos.latitude));
        longitude.setText(String.valueOf(pos.longitude));

        final TextView banking = findViewById(R.id.banking);
        banking.setText(station.isBanking()
                ? R.string.data_banking_true
                : R.string.data_banking_false);

        final TextView bonus = findViewById(R.id.bonus);
        bonus.setText(station.isBonus()
                ? R.string.data_bonus_true
                : R.string.data_bonus_false);

        final TextView bikeStands = findViewById(R.id.bikeStands);
        bikeStands.setText(String.valueOf(station.getBikeStands()));

        final TextView availableBikeStands = findViewById(R.id.availableBikeStands);
        availableBikeStands.setText(String.valueOf(station.getAvailableBikeStands()));

        final TextView availableBikes = findViewById(R.id.availableBikes);
        availableBikes.setText(String.valueOf(station.getAvailableBikes()));

        final TextView lastUpdate = findViewById(R.id.last_update);
        final DateFormat formatter = android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        lastUpdate.setText(formatter.format(station.getLastUpdate()));
    }
}
