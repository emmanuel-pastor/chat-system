package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.ObservableSet;

import java.net.Socket;
import java.util.Map;
import java.util.Set;

public interface RuntimeDataStore {
    String readUsername();

    void writeUsername(String newUsername);

    ObservableSet<User> readUsersSet();

    void addAllUsers(Set<User> newSet);

    void setUserConnectionStatus(String macAddress, boolean isConnected);

    Map<String, Socket> readOpenedSockets();

    void addOpenSocket(String key, Socket newSocket);

    void removeOpenSocket(String key);
}
