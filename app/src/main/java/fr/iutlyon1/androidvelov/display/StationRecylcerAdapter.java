package fr.iutlyon1.androidvelov.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Comparator;

import fr.iutlyon1.androidvelov.R;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

/**
 * Created by xontik on 30/03/2018.
 */

public class StationRecylcerAdapter extends RecyclerView.Adapter<StationRecylcerAdapter.ViewHolder> {

    private VelovData mDataset;
    private Comparator<VelovStationData> mComparator;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView stationName;
        public TextView stationAvailableBike;
        public TextView stationAvailableBikeStands;
        public ImageView favoriteIcon;
        public Context context;

        public ViewHolder(View v) {
            super(v);
            stationName = (TextView) v.findViewById(R.id.stationName) ;
            stationAvailableBike = (TextView) v.findViewById(R.id.stationAvailableBikes) ;
            stationAvailableBikeStands = (TextView) v.findViewById(R.id.stationAvailableBikeStands);
            favoriteIcon = v.findViewById(R.id.favoriteIcon);
            context = v.getContext();

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StationRecylcerAdapter(VelovData myDataset, Comparator<VelovStationData> comparator) {
        mDataset = myDataset;
        mComparator = comparator;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StationRecylcerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        VelovStationData currentItem = mDataset.get(position);

        holder.stationName.setText(currentItem.getName());

        holder.stationAvailableBike.setText(String.valueOf(currentItem.getAvailableBikes()));
        holder.stationAvailableBikeStands.setText(String.valueOf(currentItem.getAvailableBikeStands()));

        if(currentItem.isFavorite()){
            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setItems(VelovData items) {
        this.mDataset = items;
        mDataset.sort(this.mComparator);
        notifyDataSetChanged();
    }

    public VelovStationData getItem(int position){
        return mDataset.get(position);
    }

    public void setItem(int position, VelovStationData item) {
        mDataset.set(position, item);
        notifyItemChanged(position);
    }

    public void setComparator(Comparator<VelovStationData> c){
        this.mComparator = c;
        mDataset.sort(this.mComparator);
        notifyDataSetChanged();

    }

    public VelovData getDataset(){
        return mDataset;
    }
}