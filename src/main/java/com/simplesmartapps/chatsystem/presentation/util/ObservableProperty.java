package com.simplesmartapps.chatsystem.presentation.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ObservableProperty<T> {
    private final Map<Object, Consumer<T>> mObservers = Collections.synchronizedMap(new HashMap<>());
    private volatile T mValue;

    public ObservableProperty(T initialValue) {
        this.mValue = initialValue;
    }

    public synchronized T getValue() {
        return this.mValue;
    }

    public synchronized void setValue(T newValue) {
        this.mValue = newValue;
        mObservers.forEach(
                (observer, callback) -> callback.accept(newValue)
        );
    }

    public void observe(Object observer, Consumer<T> callback) {
        mObservers.putIfAbsent(observer, callback);
        callback.accept(mValue);
    }

    public void removeObserver(Object observer) {
        mObservers.remove(observer);
    }

    public void clearObservers() {
        mObservers.clear();
    }
}
