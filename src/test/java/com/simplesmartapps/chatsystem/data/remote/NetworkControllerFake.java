package com.simplesmartapps.chatsystem.data.remote;

import javafx.util.Pair;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;

public class NetworkControllerFake implements NetworkController {
    private static final NetworkController INSTANCE = new NetworkControllerFake();

    private NetworkControllerFake() {
    }

    public static NetworkController getInstance() {
        return INSTANCE;
    }

    @Override
    public List<JSONObject> sendBroadcastWithMultipleResponses(JSONObject message, int timeout) {
        return null;
    }

    @Override
    public List<Pair<InetAddress, String>> listAvailableNetworks() {
        InetAddress fakeAddress = InetAddress.getLoopbackAddress();
        return List.of(new Pair<>(fakeAddress, "Loopback network interface"));
    }

    @Override
    public void setBroadcastAddress(InetAddress broadcastAddress) {

    }
}
