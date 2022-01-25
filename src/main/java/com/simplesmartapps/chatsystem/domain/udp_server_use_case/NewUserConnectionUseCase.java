package com.simplesmartapps.chatsystem.domain.udp_server_use_case;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;

import java.util.Collections;

public class NewUserConnectionUseCase {
    private final RuntimeDataStore mRuntimeDataStore;
    private final NetworkController mNetworkController;

    @Inject
    public NewUserConnectionUseCase(RuntimeDataStore runtimeDataStore, NetworkController mNetworkController) {
        this.mRuntimeDataStore = runtimeDataStore;
        this.mNetworkController = mNetworkController;
    }

    public void execute(User newUser) {
        if (!mNetworkController.getMacAddress().equals(newUser.macAddress())) {
            mRuntimeDataStore.addAllUsers(Collections.singletonMap(newUser.macAddress(), newUser));
        }
    }
}
