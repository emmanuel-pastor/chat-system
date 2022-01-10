package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.exception.NetworkListingException;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;

import java.util.List;

public class ListAvailableNetworksUseCase {
    private final NetworkController mNetworkController;

    @Inject
    public ListAvailableNetworksUseCase(NetworkController netWorkController) {
        this.mNetworkController = netWorkController;
    }

    public List<BroadcastNetwork> execute() throws NetworkListingException {
        return mNetworkController.listAvailableNetworks();
    }
}
