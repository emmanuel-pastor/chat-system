package com.simplesmartapps.chatsystem.presentation.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;

public class ObservableProperty<T> {
    private final HashMap<Object, Consumer<T>> mObservers = new HashMap<>(Collections.emptyMap());
    private T mValue;

    public ObservableProperty(T initialValue) {
        this.mValue = initialValue;
    }

    public T getValue() {
        return this.mValue;
    }

    public void setValue(T newValue) {
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
}
