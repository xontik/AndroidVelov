package fr.iutlyon1.androidvelov;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import fr.iutlyon1.androidvelov.display.StationRecyclerViewAdapter;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;
import fr.iutlyon1.androidvelov.utils.LatLngUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class StationListFragment extends Fragment {
    private OnListFragmentInteractionListener mListener;
    private StationRecyclerViewAdapter mAdapter;

    private FusedLocationProviderClient mFusedLocationClient;
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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
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

        swipeRefresh.setOnRefreshListener(() -> {
            mDataset.load(
                    context,
                    (dataset) -> swipeRefresh.setRefreshing(false)
            );
            refreshLocation();
        });

        refreshLocation();
        return swipeRefresh;
    }

    private void refreshLocation() {
        if (!checkLocationPermission()) {
            mAdapter.setComparator((s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(
                            getActivity(),
                            location -> {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    mAdapter.setComparator((s1, s2) -> {
                                        double dist1 = LatLngUtils.computeDistance(
                                                currentLatLng,
                                                s1.getPosition()
                                        );
                                        double dist2 = LatLngUtils.computeDistance(
                                                currentLatLng,
                                                s2.getPosition()
                                        );

                                        double diff = dist1 - dist2;
                                        if (-1. < diff && diff < 1.) {
                                            return diff > 0 ? 1 : -1;
                                        }

                                        return (int) diff;
                                    });
                                }
                            }
                    );

        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            )) {
                // Show an explanation to the user asynchronously. After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.dialog_ok, (dialogInterface, i) -> {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentClick(int position, VelovStationData station);
        boolean onListFragmentLongClick(int position, VelovStationData station);
    }
}
