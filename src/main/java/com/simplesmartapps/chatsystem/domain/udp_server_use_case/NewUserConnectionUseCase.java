package com.simplesmartapps.chatsystem.domain.udp_server_use_case;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;

import java.util.HashSet;
import java.util.List;

public class NewUserConnectionUseCase {
    private final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public NewUserConnectionUseCase(RuntimeDataStore runtimeDataStore) {
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public void execute(User newUser) {
        mRuntimeDataStore.addAllUsers(new HashSet<>(List.of(newUser)));
    }
}
