package fr.iutlyon1.androidvelov.model;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class VelovData implements Iterable {

    private Set<VelovStationData> stations;

    public VelovData() {
        this.stations = new HashSet<>();
    }

    public int size() {
        return stations.size();
    }

    public boolean isEmpty() {
        return stations.isEmpty();
    }

    public boolean contains(Object o) {
        return stations.contains(o);
    }

    public boolean add(VelovStationData velovStationData) {
        return stations.add(velovStationData);
    }

    public boolean remove(Object o) {
        return stations.remove(o);
    }

    public boolean containsAll(@NonNull Collection<?> c) {
        return stations.containsAll(c);
    }

    public boolean addAll(@NonNull Collection<? extends VelovStationData> c) {
        return stations.addAll(c);
    }

    public boolean removeAll(@NonNull Collection<?> c) {
        return stations.removeAll(c);
    }

    @NonNull
    @Override
    public Iterator iterator() {
        return stations.iterator();
    }
}
