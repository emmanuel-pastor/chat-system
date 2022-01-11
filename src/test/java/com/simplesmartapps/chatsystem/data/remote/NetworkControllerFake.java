package com.simplesmartapps.chatsystem.data.remote;

import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastResponse;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class NetworkControllerFake implements NetworkController {
    private static final NetworkController INSTANCE = new NetworkControllerFake();

    private NetworkControllerFake() {
    }

    public static NetworkController getInstance() {
        return INSTANCE;
    }

    @Override
    public String getMacAddress() {
        return null;
    }

    @Override
    public List<BroadcastResponse> sendBroadcastWithMultipleResponses(JSONObject message, int destinationPort, int timeout) {
        return null;
    }

    @Override
    public void sendBroadcast(JSONObject message, int port) {
    }

    @Override
    public void sendUDP(JSONObject message, InetAddress inetAddress, int port) {
    }

    @Override
    public List<BroadcastNetwork> listAvailableNetworks() {
        InetAddress fakeAddress = InetAddress.getLoopbackAddress();
        return List.of(new BroadcastNetwork(fakeAddress, "Loopback network interface", new byte[]{}));
    }

    @Override
    public void setBroadcastNetwork(BroadcastNetwork broadcastAddress) {

    }
}
