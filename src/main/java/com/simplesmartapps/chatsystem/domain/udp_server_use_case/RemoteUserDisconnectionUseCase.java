package com.simplesmartapps.chatsystem.domain.udp_server_use_case;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;

public class RemoteUserDisconnectionUseCase {
    private final RuntimeDataStore mRuntimeDataStore;
    private final NetworkController mNetworkController;

    @Inject
    public RemoteUserDisconnectionUseCase(RuntimeDataStore mRuntimeDataStore, NetworkController networkController) {
        this.mRuntimeDataStore = mRuntimeDataStore;
        this.mNetworkController = networkController;
    }

    public void execute(String userId) {
        if (!userId.equals(mNetworkController.getMacAddress())) {
            mRuntimeDataStore.removeOpenSocket(userId);
            mRuntimeDataStore.setUserConnectionStatus(userId, false);
        }
    }
}
