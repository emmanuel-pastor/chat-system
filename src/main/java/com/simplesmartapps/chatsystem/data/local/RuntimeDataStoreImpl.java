package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;

public class RuntimeDataStoreImpl implements RuntimeDataStore {
    private static final RuntimeDataStore INSTANCE = new RuntimeDataStoreImpl();

    private String username = "";
    private final ObservableSet<User> usersSet = FXCollections.observableSet();

    private RuntimeDataStoreImpl() {
    }

    public static RuntimeDataStore getInstance() {
        return INSTANCE;
    }

    @Override
    public synchronized String readUsername() {
        return username;
    }

    @Override
    public synchronized void writeUsername(String newUsername) {
        username = newUsername;
    }

    @Override
    public synchronized ObservableSet<User> readUsersSet() {
        return usersSet;
    }

    @Override
    public synchronized void addAllUsers(Set<User> newSet) {
        usersSet.addAll(newSet);
    }

    @Override
    public void removeUser(String macAddress) {
        usersSet.removeIf(user -> user.macAddress().equals(macAddress));
    }
}
