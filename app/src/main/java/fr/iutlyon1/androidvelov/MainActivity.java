package fr.iutlyon1.androidvelov;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            StationListFragment.OnListFragmentInteractionListener,
            StationMapFragment.OnMapFragmentInteractionListener {
    private DrawerLayout drawer;
    private SearchView mSearchView;

    private Fragment fragmentMap = null;
    private Fragment fragmentList = null;

    private VelovData mDataset;

    public MainActivity() {
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

        mDataset = (VelovData) getIntent().getSerializableExtra("dataset");
        if (mDataset == null) {
            throw new IllegalArgumentException("Need a dataset !");
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_app_bar, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    generateCursor(mDataset.getStations()),
                    new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                    new int[] { android.R.id.text1 },
                    0
            ));

            EditText searchPlate = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint(R.string.station_map_search_hint);

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    mDataset.setFilter(newText);
                    return false;
                }
            });

            mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                    String fullname = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                    cursor.close();

                    searchPlate.setText(fullname);
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    return onSuggestionSelect(position);
                }
            });


            mDataset.addOnFilterUpdateListener((filter, filteredStations, dataset) -> {
                Cursor cursor = generateCursor(filteredStations);

                mSearchView.getSuggestionsAdapter().changeCursor(cursor);
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    private Cursor generateCursor(List<VelovStationData> stations) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] { "_id", "suggest_text_1" }
        );

        for (VelovStationData station : stations) {
            cursor.newRow()
                    .add("_id", station.getNumber())
                    .add("suggest_text_1", station.getFullName());
        }

        return cursor;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
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
