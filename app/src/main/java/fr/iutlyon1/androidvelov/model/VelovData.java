package fr.iutlyon1.androidvelov.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class VelovData implements Iterable<VelovStationData> {

    private List<VelovStationData> stations;

    public VelovData() {
        this.stations = new ArrayList<>();
    }

    public VelovStationData get(int index) {
        return stations.get(index);
    }

    public VelovStationData find(int number) {
        for (VelovStationData station : stations) {
            if (station.getNumber() == number) {
                return station;
            }
        }
        return null;
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
        return stations.add(velovStationData);
    }

    public boolean addAll(@NonNull Collection<? extends VelovStationData> c) {
        return stations.addAll(c);
    }

    public boolean remove(VelovStationData station) {
        return stations.remove(station);
    }

    public boolean removeAll(@NonNull Collection<? extends VelovStationData> c) {
        return stations.removeAll(c);
    }

    @NonNull
    @Override
    public Iterator<VelovStationData> iterator() {
        return stations.iterator();
    }

}
