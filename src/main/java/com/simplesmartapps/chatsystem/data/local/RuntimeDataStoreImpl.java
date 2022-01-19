package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RuntimeDataStoreImpl implements RuntimeDataStore {
    private static final RuntimeDataStore INSTANCE = new RuntimeDataStoreImpl();

    private volatile String username = "";
    private final ObservableSet<User> usersSet = FXCollections.observableSet();
    private final Map<String, Socket> openedSockets = Collections.synchronizedMap(new HashMap<>());

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
    public void addAllUsers(Set<User> newSet) {
        usersSet.addAll(newSet);
    }

    @Override
    public void modifyUser(User modifiedUser) {
        usersSet.removeIf(user -> user.macAddress().equals(modifiedUser.macAddress()));
        usersSet.add(modifiedUser);
    }

    @Override
    public Map<String, Socket> readOpenedSockets() {
        return openedSockets;
    }

    @Override
    public void addOpenSocket(String key, Socket newSocket) {
        openedSockets.putIfAbsent(key, newSocket);
    }

    @Override
    public void removeOpenSocket(String key) {
        openedSockets.remove(key);
    }
}
