package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RuntimeDataStoreImpl implements RuntimeDataStore {
    private static final RuntimeDataStore INSTANCE = new RuntimeDataStoreImpl();

    private volatile String username = "";
    private final ObservableMap<String, User> knownUsers = FXCollections.observableHashMap();
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
    public synchronized ObservableMap<String, User> readKnownUsers() {
        return knownUsers;
    }

    @Override
    public void addAllUsers(Map<String, User> newUsers) {
        Platform.runLater(() -> knownUsers.putAll(newUsers));
    }

    @Override
    public void setUserConnectionStatus(String macAddress, boolean isConnected) {
        Platform.runLater(() -> {
            User userToModify = knownUsers.get(macAddress);
            knownUsers.put(
                    macAddress,
                    new User(
                            userToModify.macAddress(),
                            userToModify.username(),
                            userToModify.ipAddress(),
                            isConnected)
            );
        });
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
