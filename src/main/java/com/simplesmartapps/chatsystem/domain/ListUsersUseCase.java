package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.ObservableMap;

public class ListUsersUseCase {
    private final RuntimeDataStore runtimeDataStore;

    @Inject
    public ListUsersUseCase(RuntimeDataStore runtimeDataStore) {
        this.runtimeDataStore = runtimeDataStore;
    }

    public ObservableMap<String, User> execute() {
        return runtimeDataStore.readKnownUsers();
    }
}
