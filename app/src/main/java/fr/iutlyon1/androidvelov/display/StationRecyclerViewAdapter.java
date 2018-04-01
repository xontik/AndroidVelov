package fr.iutlyon1.androidvelov.display;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;

import fr.iutlyon1.androidvelov.R;
import fr.iutlyon1.androidvelov.StationListFragment.OnListFragmentInteractionListener;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

/**
 * {@link RecyclerView.Adapter} that can display a {@link VelovStationData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.ViewHolder> {

    private final VelovData mDataset;
    private final OnListFragmentInteractionListener mListener;
    private Comparator<VelovStationData> mComparator;

    public StationRecyclerViewAdapter(VelovData dataset, OnListFragmentInteractionListener listener) {
        mDataset = dataset;
        mListener = listener;
        mComparator = null;

        mDataset.addOnItemsUpdateListener(d -> {
            if (mComparator != null)
                d.sort(mComparator);
            notifyDataSetChanged();
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Get element from your dataset at this position
        VelovStationData currentItem = mDataset.get(position);

        // Replace the contents of the view with that element
        holder.mStationName.setText(currentItem.getName());

        holder.mStationAvailableBike.setText(String.valueOf(currentItem.getAvailableBikes()));
        holder.mStationAvailableBikeStands.setText(String.valueOf(currentItem.getAvailableBikeStands()));

        holder.mFavoriteIcon.setImageResource(currentItem.isFavorite()
                ? R.drawable.ic_favorite_black_24dp
                : R.drawable.ic_favorite_border_black_24dp);

        holder.mView.setOnClickListener(v -> {
            if (mListener != null) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentClick(position, currentItem);
            }
        });
        holder.mView.setOnLongClickListener(v ->
                mListener != null && mListener.onListFragmentLongClick(position, currentItem));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setComparator(Comparator<VelovStationData> comparator) {
        if (!mComparator.equals(comparator)) {
            this.mComparator = comparator;

            if (comparator != null)
                mDataset.sort(comparator);

            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Each data item is just a string in this case
        final View mView;
        final ImageView mFavoriteIcon;
        final TextView mStationName;
        final TextView mStationAvailableBike;
        final TextView mStationAvailableBikeStands;

        ViewHolder(View v) {
            super(v);

            mView = v;
            mFavoriteIcon = v.findViewById(R.id.favoriteIcon);
            mStationName = v.findViewById(R.id.stationName) ;
            mStationAvailableBike = v.findViewById(R.id.stationAvailableBikes) ;
            mStationAvailableBikeStands = v.findViewById(R.id.stationAvailableBikeStands);
        }
    }
}
