package com.simplesmartapps.chatsystem.data.remote;

import javafx.util.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkControllerImpl implements NetworkController {
    private static final NetworkController INSTANCE = new NetworkControllerImpl();

    private NetworkControllerImpl() {
    }

    public static NetworkController getInstance() {
        return INSTANCE;
    }

    @Override
    public JSONObject sendBroadcastWithResponse(JSONObject message, int timeout) {
        return null;
    }

    private void broadcast(String broadcastMessage, InetAddress address) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, address, 4445);
        socket.send(packet);
        socket.close();
    }

    public List<Pair<InetAddress, String>> listAvailableNetworks() throws NetworkListingException {
        List<Pair<InetAddress, String>> broadcastPair = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces;

        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                String displayName = networkInterface.getDisplayName();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                networkInterface.getInterfaceAddresses().stream()
                        .map(interfaceAddress -> new Pair<>(interfaceAddress.getBroadcast(), displayName))
                        .filter(pair -> pair.getValue() != null)
                        .forEach(broadcastPair::add);
            }
        } catch (SocketException e) {
            throw new NetworkListingException(e.getMessage());
        }

        return broadcastPair;
    }
}
