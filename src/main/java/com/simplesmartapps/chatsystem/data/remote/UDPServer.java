package com.simplesmartapps.chatsystem.data.remote;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.util.JsonUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.*;

import static com.simplesmartapps.chatsystem.Constants.UDP_SERVER_INPUT_PORT;

public class UDPServer implements Runnable {
    private final NetworkController mNetworkController;
    private final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public UDPServer(NetworkController networkController, RuntimeDataStore mRuntimeDataStore) {
        this.mNetworkController = networkController;
        this.mRuntimeDataStore = mRuntimeDataStore;
    }

    @Override
    public void run() {
        Thread receivingThread = new Thread("receiving_thread") {
            public void run() {
                try (DatagramSocket listeningSocket = new DatagramSocket(UDP_SERVER_INPUT_PORT)) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    while (true) {
                        try {
                            listeningSocket.receive(packet);
                            JSONObject packetData = JsonUtil.fromByteToJson(packet.getData());
                            String type = packetData.getString("type");
                            InetAddress packetAddress = packet.getAddress();

                            if (type.equals("USERNAME_VALIDATION")) {
                                JSONObject response = createUsernameValidationResponse();

                                mNetworkController.sendUDP(response, packetAddress, 60418);
                            } else if (type.equals("NEW_CONNECTION")) {
                                String username = packetData.getString("username");
                                String macAddress = packetData.getString("mac_address");
                                User newUser = new User(macAddress, username, packetAddress, true);

                                mRuntimeDataStore.addAllUsers(new HashSet<>(List.of(newUser)));
                            }
                        } catch (IOException e) {
                            System.out.println("An error occurred with receiving data in the UDP server");
                            e.printStackTrace();
                        }
                    }
                } catch (SocketException e) {
                    System.out.println("Could not start the UDP server");
                    e.printStackTrace();
                }
            }
        };
        receivingThread.start();
    }

    private JSONObject createUsernameValidationResponse() {
        String macAddress = mNetworkController.getMacAddress();
        String username = mRuntimeDataStore.readUsername();
        return new JSONObject().put("username", username).put("mac_address", macAddress);
    }
}
