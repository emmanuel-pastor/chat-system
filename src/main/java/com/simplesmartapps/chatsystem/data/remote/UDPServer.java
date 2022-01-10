package com.simplesmartapps.chatsystem.data.remote;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.remote.util.JsonUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;

public class UDPServer implements Runnable {
    private final DatagramSocket mUsernameValidationSocket;
    private final byte[] buffer = new byte[1024];
    private final NetworkController mNetworkController;

    @Inject
    public UDPServer(NetworkController networkController) throws SocketException {
        this.mNetworkController = networkController;
        this.mUsernameValidationSocket = new DatagramSocket(4445);
    }

    @Override
    public void run() {
        Thread receive = new Thread("receive_thread") {
            public void run() {
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        mUsernameValidationSocket.receive(packet);
                        JSONObject packetData = JsonUtil.fromByteToJson(packet.getData());
                        InetAddress packetAddress = packet.getAddress();
                        JSONObject response = generateJSONResponse();
                        mNetworkController.sendUDP(response, packetAddress, 60418);
                        System.out.println("Packet sent to" + packet.getAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receive.start();
    }

    public JSONObject generateJSONResponse() throws SocketException, UnknownHostException {
        JSONObject jsonResponse = new JSONObject();
        String macAddress = mNetworkController.getLocalhostMacAddress();
        String ipAddress = InetAddress.getLocalHost().toString().split("/")[1];
        String username = "Romain";
        jsonResponse.put("mac_address", macAddress);
        jsonResponse.put("username", username);
        jsonResponse.put("ip_address", ipAddress);
        return jsonResponse;
    }


}
