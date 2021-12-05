package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.NetworkListingException;
import javafx.util.Pair;

import java.net.InetAddress;
import java.util.List;

public class ListAvailableNetworksUseCase {
    private final NetworkController mNetworkController;

    @Inject
    public ListAvailableNetworksUseCase(NetworkController netWorkController) {
        this.mNetworkController = netWorkController;
    }

    public List<Pair<InetAddress, String>> execute() throws NetworkListingException {
        return mNetworkController.listAvailableNetworks();
    }
}
