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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.iutlyon1.androidvelov.R;
import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.utils.InternetUtils;

public class VelovData implements Iterable<VelovStationData>, Serializable {
    public interface FilterUpdateListener {
        void onFilterUpdate(String filter, List<VelovStationData> filteredStations, VelovData dataset);
    }

    private static final String TAG = "VelovData";
    private static final String SAVE_FILE = "dataset.ser";

    private List<VelovStationData> mStations;

    private transient String mFilter;
    private transient Comparator<VelovStationData> mComparator = null;

    private transient Date lastLoadDate = null;

    private transient List<FilterUpdateListener> filterUpdateListeners;

    public VelovData() {
        this.mStations = new ArrayList<>();
        this.mFilter = null;


        this.filterUpdateListeners = new LinkedList<>();
    }

    public void set(int index, VelovStationData item) throws IndexOutOfBoundsException {
        mStations.set(index, item);
        notifyFilterUpdated();
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
        notifyFilterUpdated();
    }

    public void load(@NonNull Context context, VelovRequest.OnTaskCompleted onTaskCompleted, boolean force) {
        // Only load if most recent datas are older than 1 minute, or if load is forced
        if (!force && lastLoadDate != null) {
            Calendar aMinuteAgo = Calendar.getInstance();
            aMinuteAgo.set(Calendar.MINUTE, aMinuteAgo.get(Calendar.MINUTE) - 1);

            if (lastLoadDate.after(aMinuteAgo.getTime())) {
                return;
            }
        }

        if (InternetUtils.isNetworkAvailable(context)) {
            VelovRequest.OnTaskCompleted modifiedListener = dataset -> {
                lastLoadDate = Calendar.getInstance().getTime();

                if (onTaskCompleted != null) {
                    onTaskCompleted.onTaskCompleted(dataset);
                }
            };

            VelovRequest req = new VelovRequest(context,"Lyon", modifiedListener);
            req.execute(this);
        } else {
            Toast.makeText(context, R.string.toast_loadSavedData , Toast.LENGTH_SHORT)
                    .show();
            loadFromSave(context);
        }
    }

    public void load(@NonNull Context context, VelovRequest.OnTaskCompleted onTaskCompleted) {
        load(context, onTaskCompleted, false);
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

        this.lastLoadDate = Calendar.getInstance().getTime();

        this.filterUpdateListeners = new LinkedList<>();
    }

    public void addOnFilterUpdateListener(FilterUpdateListener listener) {
        this.filterUpdateListeners.add(listener);
    }

    public void removeOnFilterUpdateListener(FilterUpdateListener listener) {
        this.filterUpdateListeners.remove(listener);
    }

    private void notifyFilterUpdated() {
        if (!filterUpdateListeners.isEmpty()) {
            List<VelovStationData> filtered = find(mFilter);

            for (FilterUpdateListener listener : filterUpdateListeners) {
                listener.onFilterUpdate(mFilter, filtered, this);
            }
        }
    }

    public void setFilter(String filter) {
        mFilter = filter;
        notifyFilterUpdated();
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

        notifyFilterUpdated();
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
