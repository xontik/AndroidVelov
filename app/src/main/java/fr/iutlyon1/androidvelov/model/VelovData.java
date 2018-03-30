package fr.iutlyon1.androidvelov.model;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import fr.iutlyon1.androidvelov.utils.LatLngUtils;

public class VelovData implements Iterable<VelovStationData>, Serializable {
    public interface ItemUpdateListener {
        void onItemUpdate(VelovData velovData);
    }

    public interface FirstLoadListener {
        void onFirstLoad(VelovData velovData);
    }

    private List<VelovStationData> stations = null;
    private transient boolean wasLoaded;

    private transient List<ItemUpdateListener> itemUpdateListeners = null;
    private transient List<FirstLoadListener> firstLoadListeners = null;


    public VelovData() {
        this.stations = new ArrayList<>();
        this.wasLoaded = false;

        this.itemUpdateListeners = new ArrayList<>();
        this.firstLoadListeners = new ArrayList<>();
    }

    public VelovStationData get(int index) {
        return stations.get(index);
    }
    public void set(int index, VelovStationData item) {
        stations.set(index, item);
    }

    public List<VelovStationData> getStations() {
        return stations;
    }

    public List<VelovStationData> find(String searchString) {
        List<VelovStationData> stationList = new ArrayList<>();

        for (VelovStationData station : stations) {
            if (station.matches(searchString)) {
                stationList.add(station);
            }
        }

        return stationList;
    }

    public VelovStationData getNearest(LatLng position) {
        if (isEmpty()) {
            return null;
        }

        final Iterator<VelovStationData> it = stations.iterator();

        VelovStationData nearest = it.next();
        double nearestDistance = LatLngUtils.computeDistance(position, nearest.getPosition());

        while (it.hasNext()) {
            VelovStationData station = it.next();
            double distance = LatLngUtils.computeDistance(position, station.getPosition());

            if (distance < nearestDistance) {
                nearest = station;
                nearestDistance = distance;
            }
        }

        return nearest;
    }



    public void addOnItemsUpdateListener(ItemUpdateListener listener) {
        this.itemUpdateListeners.add(listener);
    }

    public void removeOnItemsUpdateListener(ItemUpdateListener listener) {
        this.itemUpdateListeners.remove(listener);
    }

    public void addOnFirstLoadListener(FirstLoadListener listener) {
        if (wasLoaded)
            listener.onFirstLoad(this);
        else
            firstLoadListeners.add(listener);
    }

    private void notifyItemsUpdated() {
        for (ItemUpdateListener listener : itemUpdateListeners) {
            listener.onItemUpdate(this);
        }
    }

    private void notifyFirstLoad() {
        wasLoaded = true;
        for (FirstLoadListener listener : firstLoadListeners) {
            listener.onFirstLoad(this);
        }
        firstLoadListeners.clear();
    }

    public int size() {
        return stations.size();
    }

    public boolean isEmpty() {
        return stations.isEmpty();
    }

    public boolean contains(VelovStationData station) {
        return stations.contains(station);
    }

    public boolean add(VelovStationData velovStationData) {
        boolean wasAdded = stations.add(velovStationData);

        if (!wasLoaded)
            notifyFirstLoad();
        if (wasAdded)
            notifyItemsUpdated();

        return wasAdded;
    }

    public boolean addAll(@NonNull Collection<? extends VelovStationData> c) {
        boolean wasAdded = stations.addAll(c);

        if (wasLoaded)
            notifyFirstLoad();
        if (wasAdded)
            notifyItemsUpdated();

        return wasAdded;
    }

    public void setAll(@NonNull Collection<? extends VelovStationData> c) {
        this.stations.clear();
        this.stations.addAll(c);

        if (!wasLoaded)
            notifyFirstLoad();
        notifyItemsUpdated();
    }

    public boolean remove(VelovStationData station) {
        boolean wasRemoved = stations.remove(station);
        if (wasRemoved) {
            notifyItemsUpdated();
        }
        return wasRemoved;
    }

    public boolean removeAll(@NonNull Collection<? extends VelovStationData> c) {
        boolean wasRemoved = stations.removeAll(c);
        if (wasRemoved) {
            notifyItemsUpdated();
        }
        return wasRemoved;
    }

    public void sort(Comparator<VelovStationData> c){
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.i("XTK", "On a le droit de sort !!!!");
            //stations.sort(Comparator.comparing(VelovStationData::isFavorite).reversed().thenComparing(VelovStationData::getName));
            //TODO: BUg a corriger voir avec enzo

        }
        */

        Collections.sort(stations, (o1, o2) -> {
            if(o1.isFavorite()){
                if(!o2.isFavorite()){
                    return -1;
                }
            } else {
                if(o2.isFavorite()){
                    return 1;
                }
            }
            if(c == null){
                return 0;
            }

            return c.compare(o1,o2);


        } );
    }

    public void sort(){
        this.sort(null);
    }

    @NonNull
    @Override
    public Iterator<VelovStationData> iterator() {
        return stations.iterator();
    }

    @Override
    public String toString() {
        return "VelovData{" +
                "stations=" + stations +
                '}';
    }
}
