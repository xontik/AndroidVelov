package fr.iutlyon1.androidvelov.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.iutlyon1.androidvelov.R;
import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.utils.InternetUtils;

public class VelovData implements Iterable<VelovStationData>, Serializable {
    public interface FirstLoadListener {
        void onFirstLoad(VelovData dataset);
    }

    public interface ItemUpdateListener {
        void onItemUpdate(VelovData dataset);
    }

    public interface FilterUpdateListener {
        void onFilterUpdate(String filter, List<VelovStationData> filteredStations, VelovData dataset);
    }

    private static final String TAG = "VelovData";
    private static final String SAVE_FILE = "dataset.ser";

    private List<VelovStationData> mStations;

    private transient String mFilter;
    private transient Comparator<VelovStationData> mComparator = null;
    private transient boolean wasLoaded;

    private transient List<FirstLoadListener> firstLoadListeners;
    private transient List<ItemUpdateListener> itemUpdateListeners;
    private transient List<FilterUpdateListener> filterUpdateListeners;

    public VelovData() {
        this.mStations = new ArrayList<>();
        this.mFilter = null;

        this.wasLoaded = false;

        this.firstLoadListeners = new LinkedList<>();
        this.itemUpdateListeners = new LinkedList<>();
        this.filterUpdateListeners = new LinkedList<>();
    }

    public VelovStationData get(int index) throws IndexOutOfBoundsException {
        return mStations.get(index);
    }

    public void set(int index, VelovStationData item) throws IndexOutOfBoundsException {
        if (!wasLoaded) {
            throw new IllegalStateException("Data aren't loaded");
        }

        mStations.set(index, item);
        notifyItemsUpdated();
    }

    public List<VelovStationData> getStations() {
        return mStations;
    }

    public List<VelovStationData> find(String searchString) {
        List<VelovStationData> stations = new ArrayList<>();

        for (VelovStationData station : this.mStations) {
            if (station.matches(searchString)) {
                stations.add(station);
            }
        }

        return stations;
    }

    public void setComparator(Comparator<VelovStationData> comparator) {
        mComparator = (s1, s2) -> {
            if (s1.isFavorite() != s2.isFavorite()) {
                return s1.isFavorite() ? -1 : 1;
            }

            if (comparator != null) {
                return comparator.compare(s1, s2);
            }

            return 0;
        };

        Collections.sort(mStations, mComparator);
        notifyItemsUpdated();
    }

    public void load(@NonNull Context context, VelovRequest.OnTaskCompleted onTaskCompleted) {
        if (InternetUtils.isNetworkAvailable(context)) {
            VelovRequest req = new VelovRequest(context, "Lyon", onTaskCompleted);
            req.execute(this);
        } else {
            Toast.makeText(context, R.string.toast_loadSavedData , Toast.LENGTH_SHORT)
                    .show();
            loadFromSave(context);
        }
    }

    private void loadFromSave(@NonNull Context context) {
        InputStream in;
        ObjectInputStream ois;
        VelovData savedData = null;

        try {
            in = context.openFileInput(SAVE_FILE);
            ois = new ObjectInputStream(in);

            savedData = (VelovData) ois.readObject();

            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            Log.e(TAG, "loadFromSave: " + e.getMessage(), e);
        }

        if (savedData != null) {
            this.setAll(savedData.getStations());
        }
    }

    public void save(@NonNull Context context) {
        OutputStream out;
        ObjectOutputStream oos;

        try {
            out = context.openFileOutput(SAVE_FILE, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(out);

            oos.writeObject(this);

            oos.close();
        } catch (IOException e) {
            Log.e(TAG, "onStop: " + e.getMessage(), e);
        }
    }

    public void processFavorites(@NonNull Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedPrefFile),
                Context.MODE_PRIVATE);

        HashSet<String> favoritesString = (HashSet<String>) sharedPref.getStringSet(
                context.getString(R.string.sharedPrefFavorites),
                new HashSet<>());

        for (VelovStationData station : this.mStations) {
            station.setFavorite(
                    favoritesString.contains(String.valueOf(station.getNumber()))
            );
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        this.mFilter = null;
        this.mComparator = null;

        this.wasLoaded = false;

        this.firstLoadListeners = new LinkedList<>();
        this.itemUpdateListeners = new LinkedList<>();
        this.filterUpdateListeners = new LinkedList<>();
    }

    public void addOnFirstLoadListener(FirstLoadListener listener) {
        if (wasLoaded) {
            listener.onFirstLoad(this);
        } else {
            firstLoadListeners.add(listener);
        }
    }

    public void addOnItemsUpdateListener(ItemUpdateListener listener) {
        this.itemUpdateListeners.add(listener);
    }

    public void removeOnItemsUpdateListener(ItemUpdateListener listener) {
        this.itemUpdateListeners.remove(listener);
    }

    public void addOnFilterUpdateListener(FilterUpdateListener listener) {
        this.filterUpdateListeners.add(listener);
    }

    public void removeOnFilterUpdateListener(FilterUpdateListener listener) {
        this.filterUpdateListeners.remove(listener);
    }

    private void notifyFirstLoad() {
        wasLoaded = true;
        for (FirstLoadListener listener : firstLoadListeners) {
            listener.onFirstLoad(this);
        }
        firstLoadListeners.clear();
    }

    private void notifyItemsUpdated() {
        for (ItemUpdateListener listener : itemUpdateListeners) {
            listener.onItemUpdate(this);
        }
        notifyFilterUpdate();
    }

    private void notifyFilterUpdate() {
        if (!filterUpdateListeners.isEmpty()) {
            List<VelovStationData> filtered = find(mFilter);

            for (FilterUpdateListener listener : filterUpdateListeners) {
                listener.onFilterUpdate(mFilter, filtered, this);
            }
        }
    }

    public void setFilter(String filter) {
        mFilter = filter;
        notifyFilterUpdate();
    }

    public String getFilter() {
        return mFilter;
    }

    public int size() {
        return mStations.size();
    }

    public boolean isEmpty() {
        return mStations.isEmpty();
    }

    public void setAll(@NonNull Collection<? extends VelovStationData> c) {
        this.mStations.clear();
        this.mStations.addAll(c);

        if (mComparator != null)
            Collections.sort(mStations, mComparator);

        if (!wasLoaded)
            notifyFirstLoad();
        notifyItemsUpdated();
    }

    @NonNull
    @Override
    public Iterator<VelovStationData> iterator() {
        return mStations.iterator();
    }

    @Override
    public String toString() {
        return "VelovData{" +
                "stations=" + mStations +
                '}';
    }
}
