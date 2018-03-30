package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import java.util.HashSet;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.display.StationRecylcerAdapter;
import fr.iutlyon1.androidvelov.listener.RecyclerItemClickListener;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;


public class StationListActivity extends AppCompatActivity implements VelovRequest.OnTaskCompleted {





    private VelovData velovData;
    private RecyclerView mRecyclerView;
    private StationRecylcerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefresh;

    public StationListActivity() {
        velovData = new VelovData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefresh = findViewById(R.id.swipeRefreshLayout);


        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mAdapter = new StationRecylcerAdapter(new VelovData());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(StationListActivity.this, StationDetailActivity.class);
                        intent.putExtra("station", (VelovStationData) mAdapter.getItem(position));
                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        VelovStationData current = mAdapter.getItem(position);

                        Context context = getApplicationContext();
                        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.sharedPrefFile), Context.MODE_PRIVATE);
                        HashSet favoritesString = (HashSet) sharedPref.getStringSet(context.getString(R.string.sharedPrefFavorites), new HashSet<String>());
                        SharedPreferences.Editor editor = sharedPref.edit();

                        if(current.isFavorite()) {
                            current.setFavorite(false);
                            favoritesString.remove(String.valueOf(current.getNumber()));
                        } else {
                            current.setFavorite(true);
                            favoritesString.add(String.valueOf(current.getNumber()));
                        }

                        editor.clear();
                        editor.putStringSet(context.getString(R.string.sharedPrefFavorites),favoritesString);
                        editor.commit();
                        mAdapter.setItem(position, current);

                    }
                })
        );

        mSwipeRefresh.setOnRefreshListener(() -> loadVelovData());

        velovData.addOnItemsUpdateListener(mAdapter::setItems);

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

}
