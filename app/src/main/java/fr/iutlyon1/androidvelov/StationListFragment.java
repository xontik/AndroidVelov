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

import fr.iutlyon1.androidvelov.api.VelovRequest;
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
    private OnListFragmentInteractionListener mListener;
    private VelovData mDataset;
    private StationRecyclerViewAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StationListFragment() {
    }

    public static StationListFragment newInstance(VelovData dataset) {
        StationListFragment fragment = new StationListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dataset", dataset);

        fragment.setArguments(bundle);
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

        mAdapter = new StationRecyclerViewAdapter(mDataset, mListener);
        recyclerView.setAdapter(mAdapter);

        swipeRefresh.setOnRefreshListener(() -> mDataset.load(
                context,
                (dataset) -> swipeRefresh.setRefreshing(false)
        ));

        return swipeRefresh;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentClick(int position, VelovStationData station);
        boolean onListFragmentLongClick(int position, VelovStationData station);
    }
}
