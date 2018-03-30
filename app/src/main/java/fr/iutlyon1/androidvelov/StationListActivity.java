package fr.iutlyon1.androidvelov;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.display.StationRecylcerAdapter;
import fr.iutlyon1.androidvelov.listener.RecyclerItemClickListener;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;


public class StationListActivity extends AppCompatActivity {

    private VelovData velovData;
    private RecyclerView mRecyclerView;
    private StationRecylcerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    StationListActivity() {
        velovData = new VelovData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

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
                        // do whatever
                    }
                })
        );


        velovData.addOnItemsUpdateListener(mAdapter::setItems);

        loadVelovData();

    }



    private void loadVelovData() {
        final String apiKey = Props.getInstance(getApplicationContext()).get("API_KEY");

        VelovRequest velovRequest = new VelovRequest(this, "Lyon", apiKey);
        velovRequest.execute(velovData);
    }
}
