package com.simplesmartapps.chatsystem.data.remote;

import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;

public interface NetworkController {
    List<JSONObject> sendBroadcastWithMultipleResponses(JSONObject message, int timeout) throws BroadcastException;

    List<BroadcastNetwork> listAvailableNetworks() throws NetworkListingException;

    void setBroadcastAddress(InetAddress broadcastAddress);
}
