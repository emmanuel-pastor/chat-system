package com.simplesmartapps.chatsystem.domain.udp_server_use_case;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;

import java.util.HashMap;
import java.util.Map;

public class NewUsernameUseCase {
    final NetworkController mNetworkController;
    final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public NewUsernameUseCase(NetworkController networkController, RuntimeDataStore runtimeDataStore) {
        this.mNetworkController = networkController;
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public void execute(User modifiedUser) {
        if (!modifiedUser.macAddress().equals(mNetworkController.getMacAddress())) {
            Map<String, User> knownUsers = mRuntimeDataStore.readKnownUsers();
            Map<String, User> updatedMap = new HashMap<>(knownUsers);
            updatedMap.put(modifiedUser.macAddress(), modifiedUser);
            mRuntimeDataStore.addAllUsers(updatedMap);
        }
    }
}
