package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;

public class SelectNetworkUseCase {
    private final NetworkController mNetworkController;

    @Inject
    public SelectNetworkUseCase(NetworkController networkController) {
        this.mNetworkController = networkController;
    }

    public void execute(BroadcastNetwork selectedNetwork) {
        mNetworkController.setBroadcastNetwork(selectedNetwork);
    }
}
