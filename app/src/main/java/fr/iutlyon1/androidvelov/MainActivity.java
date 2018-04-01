package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Set;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            StationListFragment.OnListFragmentInteractionListener,
            StationMapFragment.OnMapFragmentInteractionListener {

    private DrawerLayout drawer;

    private Fragment fragmentMap = null;
    private Fragment fragmentList = null;

    private VelovData mDataset;

    public MainActivity() {
        mDataset = new VelovData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        showFragmentMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataset.load(this, null);
    }

    @Override
    protected void onStop() {
        mDataset.save(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_map:
                showFragmentMap();
                break;
            case R.id.nav_list:
                showFragmentList();
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFragmentMap() {
        if (this.fragmentMap == null)
            this.fragmentMap = StationMapFragment.newInstance(mDataset);
        this.startTransactionFragment(this.fragmentMap);
    }

    private void showFragmentList() {
        if (this.fragmentList == null)
            this.fragmentList = StationListFragment.newInstance(mDataset);
        this.startTransactionFragment(this.fragmentList);
    }

    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
        }
    }

    private void showStationDetails(VelovStationData station) {
        Intent intent = new Intent(MainActivity.this, StationDetailActivity.class);
        intent.putExtra("station", station);
        startActivity(intent);
    }

    @Override
    public void onInfoWindowClick(VelovStationData station) {
        showStationDetails(station);
    }

    @Override
    public void onListFragmentClick(int position, VelovStationData station) {
        showStationDetails(station);
    }

    @Override
    public boolean onListFragmentLongClick(int position, VelovStationData station) {
        Context context = getApplicationContext();

        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefFile),
                Context.MODE_PRIVATE);

        Set<String> favoritesString = sharedPref.getStringSet(
                context.getString(R.string.sharedPrefFavorites),
                new HashSet<>());

        SharedPreferences.Editor editor = sharedPref.edit();

        if (station.isFavorite()) {
            station.setFavorite(false);
            favoritesString.remove(String.valueOf(station.getNumber()));
        } else {
            station.setFavorite(true);
            favoritesString.add(String.valueOf(station.getNumber()));
        }

        editor.clear();
        editor.putStringSet(context.getString(R.string.sharedPrefFavorites),favoritesString);
        editor.apply();

        mDataset.set(position, station);
        return false;
    }
}
