package fr.iutlyon1.androidvelov;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashSet;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.display.StationRecylcerAdapter;
import fr.iutlyon1.androidvelov.listener.RecyclerItemClickListener;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.LatLngUtils;


public class StationListActivity extends AppCompatActivity implements VelovRequest.OnTaskCompleted {


    private VelovData velovData;
    private RecyclerView mRecyclerView;
    private StationRecylcerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;

    public StationListActivity() {
        velovData = new VelovData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        mLocation = null;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefresh = findViewById(R.id.swipeRefreshLayout);


        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new StationRecylcerAdapter(new VelovData(), null);


        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(StationListActivity.this, StationDetailActivity.class);
                        intent.putExtra("station", (VelovStationData) mAdapter.getItem(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        VelovStationData current = mAdapter.getItem(position);

                        Context context = getApplicationContext();
                        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.sharedPrefFile), Context.MODE_PRIVATE);
                        HashSet favoritesString = (HashSet) sharedPref.getStringSet(context.getString(R.string.sharedPrefFavorites), new HashSet<String>());
                        SharedPreferences.Editor editor = sharedPref.edit();

                        if (current.isFavorite()) {
                            current.setFavorite(false);
                            favoritesString.remove(String.valueOf(current.getNumber()));
                        } else {
                            current.setFavorite(true);
                            favoritesString.add(String.valueOf(current.getNumber()));
                        }

                        editor.clear();
                        editor.putStringSet(context.getString(R.string.sharedPrefFavorites), favoritesString);
                        editor.commit();
                        mAdapter.setItem(position, current);

                    }
                })
        );

        mSwipeRefresh.setOnRefreshListener(() -> loadVelovData());

        velovData.addOnItemsUpdateListener(mAdapter::setItems);

        if(!checkLocationPermission()){
            mAdapter.setComparator((o1,o2) -> o1.getName().compareTo(o2.getName()));
        } else {
            mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Toast.makeText(getApplicationContext(), "Location ok", Toast.LENGTH_SHORT).show();
                        LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                        mAdapter.setComparator((o1,o2) ->
                            LatLngUtils.computeDistance(o1.getPosition(), currentLatLng) < LatLngUtils.computeDistance(o2.getPosition(), currentLatLng)? -1 : 1
                        );

                    }
                });

        }

        loadVelovData();


    }


    private void loadVelovData() {
        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");

        VelovRequest velovRequest = new VelovRequest(this, "Lyon", apiKey, this);
        velovRequest.execute(velovData);

    }

    @Override
    public void onTaskCompleted() {
        mSwipeRefresh.setRefreshing(false);
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.dialog_ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(StationListActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {



                } else {
                    Toast.makeText(getApplicationContext(), "Location nok", Toast.LENGTH_SHORT).show();;

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
}
