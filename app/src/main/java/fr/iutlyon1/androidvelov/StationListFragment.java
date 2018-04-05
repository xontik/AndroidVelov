package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.iutlyon1.androidvelov.display.StationRecyclerViewAdapter;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StationListFragment extends Fragment {
    public interface OnListFragmentInteractionListener {
        void onListFragmentClick(int position, VelovStationData station);
        boolean onListFragmentLongClick(int position, VelovStationData station);
    }

    private OnListFragmentInteractionListener mListener;

    private VelovData mDataset;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StationListFragment() {
    }

    public static StationListFragment newInstance(VelovData dataset) {
        StationListFragment fragment = new StationListFragment();
        Bundle args = new Bundle();
        args.putSerializable("dataset", dataset);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("A dataset is needed");
        }

        mDataset = (VelovData) args.getSerializable("dataset");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_station_list, container, false);

        // Set the adapter
        final Context context = swipeRefresh.getContext();
        RecyclerView recyclerView = swipeRefresh.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        StationRecyclerViewAdapter mAdapter = new StationRecyclerViewAdapter(mDataset, mListener);
        recyclerView.setAdapter(mAdapter);

        swipeRefresh.setOnRefreshListener(() ->
                mDataset.load(
                        context,
                        (dataset) -> swipeRefresh.setRefreshing(false)
                )
        );

        return swipeRefresh;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
