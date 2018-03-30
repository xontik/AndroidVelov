package fr.iutlyon1.androidvelov.model;

import android.os.Build;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class VelovData implements Iterable<VelovStationData>, Serializable {
    public interface ItemUpdateListener {
        void onItemUpdate(VelovData velovData);
    }

    private List<VelovStationData> stations = null;

    private transient List<ItemUpdateListener> itemUpdateListeners = null;

    public VelovData() {
        this.stations = new ArrayList<>();
        this.itemUpdateListeners = new ArrayList<>();
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
        double nearestDistance = computeDistance(position, nearest.getPosition());

        while (it.hasNext()) {
            VelovStationData station = it.next();
            double distance = computeDistance(position, station.getPosition());

            if (distance < nearestDistance) {
                nearest = station;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    private double computeDistance(LatLng from, LatLng to) {
        double lat= to.latitude - from.latitude;
        double lng = to.longitude - from.longitude;

        return Math.sqrt(Math.pow(lat, 2) + Math.pow(lng, 2));
    }

    public void addOnItemsUpdateListener(ItemUpdateListener listener) {
        this.itemUpdateListeners.add(listener);
    }

    public void removeOnItemsUpdateListener(ItemUpdateListener listener) {
        this.itemUpdateListeners.remove(listener);
    }

    private void notifyItemsUpdated() {
        for (ItemUpdateListener listener : itemUpdateListeners) {
            listener.onItemUpdate(this);
        }
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
        if (wasAdded) {
            notifyItemsUpdated();
        }
        return wasAdded;
    }

    public boolean addAll(@NonNull Collection<? extends VelovStationData> c) {
        boolean wasAdded = stations.addAll(c);
        if (wasAdded) {
            notifyItemsUpdated();
        }
        return wasAdded;
    }

    public void setAll(@NonNull Collection<? extends VelovStationData> c) {
        this.stations.clear();
        this.stations.addAll(c);
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

    public void sort(){
        if (Build.VERSION.SDK_INT >= 24) {
            stations.sort(Comparator.comparing(VelovStationData::isFavorite).reversed().thenComparing(VelovStationData::getName));
        }
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
