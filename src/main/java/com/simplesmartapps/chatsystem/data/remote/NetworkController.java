package com.simplesmartapps.chatsystem.data.remote;

import javafx.util.Pair;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;

public interface NetworkController {
    JSONObject sendBroadcastWithResponse(JSONObject message, int timeout) throws BroadcastException;

    List<Pair<InetAddress, String>> listAvailableNetworks() throws NetworkListingException;
}
