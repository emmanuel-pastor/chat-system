package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.ObservableSet;

import java.util.Set;

public interface RuntimeDataStore {
    String readUsername();
    void writeUsername(String newUsername);

    ObservableSet<User> readUsersSet();

    void addAllUsers(Set<User> newSet);
    void removeUser(String macAddress);
}
