package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.ObservableMap;

import java.net.Socket;
import java.util.Map;

public interface RuntimeDataStore {
    String readUsername();

    void writeUsername(String newUsername);

    ObservableMap<String, User> readKnownUsers();

    void addAllUsers(Map<String, User> newUsers);

    void setUserConnectionStatus(String macAddress, boolean isConnected);

    Map<String, Socket> readOpenedSockets();

    void addOpenSocket(String key, Socket newSocket);

    void removeOpenSocket(String key);
}
