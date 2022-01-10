package com.simplesmartapps.chatsystem.data.remote;

import com.simplesmartapps.chatsystem.data.remote.exception.BroadcastException;
import com.simplesmartapps.chatsystem.data.remote.exception.NetworkListingException;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public interface NetworkController {
    String getLocalhostMacAddress() throws UnknownHostException, SocketException;

    List<BroadcastResponse> sendBroadcastWithMultipleResponses(JSONObject message, int timeout) throws BroadcastException;

    void sendBroadcast(JSONObject message, int port) throws BroadcastException;

    void sendUDP(JSONObject message, InetAddress inetAddress, int port) throws IOException;

    List<BroadcastNetwork> listAvailableNetworks() throws NetworkListingException;

    void setBroadcastAddress(InetAddress broadcastAddress);
}
