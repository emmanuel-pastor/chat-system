package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;

import java.net.InetAddress;

public class SelectNetworkUseCase {
    private final NetworkController mNetworkController;

    @Inject
    public SelectNetworkUseCase(NetworkController networkController) {
        this.mNetworkController = networkController;
    }

    public void execute(InetAddress selectedNetwork) {
        mNetworkController.setBroadcastAddress(selectedNetwork);
    }
}
