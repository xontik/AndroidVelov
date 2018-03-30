package fr.iutlyon1.androidvelov.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import fr.iutlyon1.androidvelov.R;
import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

/**
 * Created by xontik on 30/03/2018.
 */

public class StationRecylcerAdapter extends RecyclerView.Adapter<StationRecylcerAdapter.ViewHolder> {

    private VelovData mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView stationName;
        public TextView stationStatus;
        public TextView stationAvailableBike;
        public TextView stationAvailableBikeStands;
        public Context context;

        public ViewHolder(View v) {
            super(v);
            stationName = (TextView) v.findViewById(R.id.stationName) ;
            stationStatus = (TextView) v.findViewById(R.id.stationStatus) ;
            stationAvailableBike = (TextView) v.findViewById(R.id.stationAvailableBikes) ;
            stationAvailableBikeStands = (TextView) v.findViewById(R.id.stationAvailableBikeStands) ;
            context = v.getContext();

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StationRecylcerAdapter(VelovData myDataset) {
        mDataset = myDataset;
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

        holder.stationStatus.setText(holder.context.getString(R.string.listItemStatus, currentItem.getStatus()));
        holder.stationAvailableBike.setText(holder.context.getString(R.string.listItemAvailableBikes , currentItem.getAvailableBikes()));
        holder.stationAvailableBikeStands.setText(holder.context.getString(R.string.listItemAvailableBikeStands, currentItem.getAvailableBikeStands()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setItems(VelovData items) {
        this.mDataset = items;
        notifyDataSetChanged();
    }

    public VelovStationData getItem(int position){
        return mDataset.get(position);
    }
}