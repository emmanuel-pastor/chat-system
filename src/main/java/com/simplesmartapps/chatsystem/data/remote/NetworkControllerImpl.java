package com.simplesmartapps.chatsystem.data.remote;

import javafx.util.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkControllerImpl implements NetworkController {
    private static final NetworkController INSTANCE = new NetworkControllerImpl();
    private InetAddress mBroadcastAddress;

    private NetworkControllerImpl() {
    }

    public static NetworkController getInstance() {
        return INSTANCE;
    }

    @Override
    public void setBroadcastAddress(InetAddress broadcastAddress) {
        this.mBroadcastAddress = broadcastAddress;
    }

    @Override
    public List<JSONObject> sendBroadcastWithMultipleResponses(JSONObject message, int timeout) throws BroadcastException {
        List<JSONObject> responseList = new ArrayList<>(Collections.emptyList());
        try (DatagramSocket serverSocket = new DatagramSocket(60418)) {
            serverSocket.setSoTimeout(timeout);
            broadcast(message.toString());
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    serverSocket.receive(receivePacket);
                    String jsonString = new String(receivePacket.getData());
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    responseList.add(jsonResponse);
                }

            } catch (SocketTimeoutException e) {
                /* Intended to happen */
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BroadcastException(e.getMessage());
        }
        return responseList;
    }

    private void broadcast(String broadcastMessage) throws IOException {
        assert (mBroadcastAddress != null);
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet
                = new DatagramPacket(buffer, buffer.length, mBroadcastAddress, 4445);
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
                        .filter(pair -> pair.getKey() != null)
                        .forEach(broadcastPair::add);
            }
        } catch (SocketException e) {
            throw new NetworkListingException(e.getMessage());
        }

        return broadcastPair;
    }
}
