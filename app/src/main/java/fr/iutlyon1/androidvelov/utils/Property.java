package fr.iutlyon1.androidvelov.utils;

import java.util.ArrayList;
import java.util.List;

public class Property<T> {
    public interface OnChangeListener<T> {
        void valueChanged(T oldValue, T newValue);
    }

    private List<OnChangeListener<T>> onChangeListeners;

    private T mProperty;

    public Property() {
        this(null);
    }

    public Property(T initialValue) {
        mProperty = initialValue;
        onChangeListeners = new ArrayList<>();
    }

    public T get() {
        return mProperty;
    }

    public void set(T newValue) {
        T oldValue = mProperty;
        mProperty = newValue;

        for (OnChangeListener<T> listener : onChangeListeners) {
            listener.valueChanged(oldValue, newValue);
        }
    }

    public boolean isNull() {
        return mProperty == null;
    }

    public void addOnChangeListener(OnChangeListener<T> listener) {
        onChangeListeners.add(listener);
    }

    public void removeOnChangeListener(OnChangeListener<T> listener) {
        onChangeListeners.remove(listener);
    }
}
