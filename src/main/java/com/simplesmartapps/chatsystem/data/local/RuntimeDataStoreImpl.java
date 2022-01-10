package com.simplesmartapps.chatsystem.data.local;

import com.simplesmartapps.chatsystem.data.local.model.User;

import java.util.Collections;
import java.util.Set;

public class RuntimeDataStoreImpl implements RuntimeDataStore {
    private static final RuntimeDataStore INSTANCE = new RuntimeDataStoreImpl();

    private String username = "";
    private Set<User> usersSet = Collections.synchronizedSet(Collections.emptySet());

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
    public synchronized Set<User> readUsersSet() {
        return usersSet;
    }

    @Override
    public synchronized void writeUsersSet(Set<User> newSet) {
        usersSet = newSet;
    }
}
