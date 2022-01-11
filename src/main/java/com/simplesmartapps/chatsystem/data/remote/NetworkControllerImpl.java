package com.simplesmartapps.chatsystem.data.remote;

import com.simplesmartapps.chatsystem.data.remote.exception.BroadcastException;
import com.simplesmartapps.chatsystem.data.remote.exception.NetworkListingException;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class NetworkControllerImpl implements NetworkController {
    private static final NetworkController INSTANCE = new NetworkControllerImpl();
    private BroadcastNetwork mBroadcastNetwork;

    private NetworkControllerImpl() {
    }

    public static NetworkController getInstance() {
        return INSTANCE;
    }

    @Override
    public void setBroadcastNetwork(BroadcastNetwork broadcastNetwork) {
        this.mBroadcastNetwork = broadcastNetwork;
    }

    @Override
    public String getMacAddress() {
        /* Format each byte in the array to a hexadecimal number using String#format. */
        byte[] hardwareAddress = mBroadcastNetwork.macAddress();
        String[] hexadecimal = new String[hardwareAddress.length];
        for (int i = 0; i < hardwareAddress.length; i++) {
            hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
        }
        return String.join("-", hexadecimal);
    }

    @Override
    public List<BroadcastResponse> sendBroadcastWithMultipleResponses(JSONObject message, int destinationPort, int timeout) throws BroadcastException {
        List<BroadcastResponse> responseList = new ArrayList<>(Collections.emptyList());
        try (DatagramSocket listeningSocket = new DatagramSocket(60418)) {
            listeningSocket.setSoTimeout(timeout);
            sendBroadcast(message, destinationPort);
            try {
                byte[] receivedData = new byte[1024];
                while (true) {
                    DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);
                    listeningSocket.receive(receivedPacket);
                    String jsonString = new String(receivedPacket.getData());
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    InetAddress address = receivedPacket.getAddress();
                    responseList.add(new BroadcastResponse(address, jsonResponse));
                }

            } catch (SocketTimeoutException e) {
                /* Intended to happen */
            }
        } catch (IOException e) {
            throw new BroadcastException("Error while creating the socket", e);
        }
        return responseList;
    }

    @Override
    public void sendBroadcast(JSONObject message, int destinationPort) throws BroadcastException {
        assert (mBroadcastNetwork != null);
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            byte[] buffer = message.toString().getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mBroadcastNetwork.address(), destinationPort);
            socket.send(packet);
        } catch (IOException e) {
            throw new BroadcastException("Error while creating the socket", e);
        }
    }

    public void sendUDP(JSONObject message, InetAddress inetAddress, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        byte[] buffer = message.toString().getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
        socket.send(packet);
        socket.close();
    }

    public List<BroadcastNetwork> listAvailableNetworks() throws NetworkListingException {
        List<BroadcastNetwork> broadcastNetworks = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces;

        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                String displayName = networkInterface.getDisplayName();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                networkInterface.getInterfaceAddresses().stream()
                        .map(interfaceAddress -> new BroadcastNetwork(interfaceAddress.getBroadcast(), displayName, hardwareAddress))
                        .filter(network -> network.address() != null)
                        .forEach(broadcastNetworks::add);
            }
        } catch (SocketException e) {
            throw new NetworkListingException("Could not get the available networks", e);
        }

        return broadcastNetworks;
    }
}
