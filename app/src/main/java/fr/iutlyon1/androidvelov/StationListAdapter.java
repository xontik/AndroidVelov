package fr.iutlyon1.androidvelov;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import fr.iutlyon1.androidvelov.model.VelovData;
import fr.iutlyon1.androidvelov.model.VelovStationData;

public class StationListAdapter extends BaseAdapter {

    private VelovData items;
    private Context context;

    public StationListAdapter(Context context, VelovData items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    public void setItems(VelovData items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).
                    inflate(R.layout.list_item, viewGroup, false);
        }

        VelovStationData currentItem = (VelovStationData) getItem(position);

        TextView stationName = (TextView)
                view.findViewById(R.id.stationName);
        TextView stationStatus = (TextView)
                view.findViewById(R.id.stationStatus);
        TextView stationAvailableBike = (TextView)
                view.findViewById(R.id.stationAvailableBikes);
        TextView stationAvailableBikeStands = (TextView)
                view.findViewById(R.id.stationAvailableBikeStands);

        stationName.setText(currentItem.getName());
        stationStatus.setText(String.format("%s%s", context.getString(R.string.listItemStatus), currentItem.getStatus()));
        stationAvailableBike.setText(String.format("%s%s", context.getString(R.string.listItemAvailableBikes), String.valueOf(currentItem.getAvailableBikes())));
        stationAvailableBikeStands.setText(String.format("%s%s", context.getString(R.string.listItemAvailableBikeStands), String.valueOf(currentItem.getAvailableBikeStands())));

        return view;
    }
}
