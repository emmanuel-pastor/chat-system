package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;

import java.util.Set;

public interface RuntimeDataStore {
    String readUsername();
    void writeUsername(String newUsername);

    Set<User> readUsersSet();
    void writeUsersSet(Set<User> newSet);
}