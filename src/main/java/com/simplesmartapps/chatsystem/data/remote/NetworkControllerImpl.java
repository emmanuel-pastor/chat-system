package com.simplesmartapps.chatsystem.data.remote;

import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastResponse;
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
    public String getLocalhostMacAddress() throws UnknownHostException, SocketException {
        InetAddress localHost = InetAddress.getLocalHost();
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(mBroadcastAddress);
        byte[] hardwareAddress = networkInterface.getHardwareAddress();

        /* Format each byte in the array to a hexadecimal number using String#format. */
        String[] hexadecimal = new String[hardwareAddress.length];
        for (int i = 0; i < hardwareAddress.length; i++) {
            hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
        }

        return String.join("-", hexadecimal);
    }

    @Override
    public List<BroadcastResponse> sendBroadcastWithMultipleResponses(JSONObject message, int timeout) throws BroadcastException {
        List<BroadcastResponse> responseList = new ArrayList<>(Collections.emptyList());
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
                    InetAddress address = receivePacket.getAddress();
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

    public void sendUDP(JSONObject message, InetAddress inetAddress, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        byte[] buffer = message.toString().getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inetAddress, port);
        socket.send(packet);
        socket.close();
    }

    private void broadcast(String broadcastMessage) throws IOException {
        assert (mBroadcastAddress != null);
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        byte[] buffer = broadcastMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, mBroadcastAddress, 4445);
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

                networkInterface.getInterfaceAddresses().stream()
                        .map(interfaceAddress -> new BroadcastNetwork(interfaceAddress.getBroadcast(), displayName))
                        .filter(network -> network.address() != null)
                        .forEach(broadcastNetworks::add);
            }
        } catch (SocketException e) {
            throw new NetworkListingException("Could not get the available networks", e);
        }

        return broadcastNetworks;
    }
}
