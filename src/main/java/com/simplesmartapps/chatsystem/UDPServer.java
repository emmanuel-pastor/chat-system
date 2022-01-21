package com.simplesmartapps.chatsystem;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.util.JsonUtil;
import com.simplesmartapps.chatsystem.domain.udp_server_use_case.NewUserConnectionUseCase;
import com.simplesmartapps.chatsystem.domain.udp_server_use_case.UsernameValidationUseCase;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static com.simplesmartapps.chatsystem.Constants.UDP_SERVER_INPUT_PORT;

public class UDPServer {
    private final UsernameValidationUseCase mUsernameValidationUseCase;
    private final NewUserConnectionUseCase mNewUserConnectionUseCase;
    DatagramSocket listeningSocket;

    @Inject
    public UDPServer(UsernameValidationUseCase usernameValidationUseCase, NewUserConnectionUseCase newUserConnectionUseCase) {
        this.mUsernameValidationUseCase = usernameValidationUseCase;
        this.mNewUserConnectionUseCase = newUserConnectionUseCase;
    }

    public void start() throws SocketException {
        listeningSocket = new DatagramSocket(UDP_SERVER_INPUT_PORT);

        Thread receivingThread = new Thread("udp_server_thread") {
            public void run() {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    while (true) {
                        listeningSocket.receive(packet);
                        JSONObject packetData = JsonUtil.fromByteToJson(packet.getData());
                        String type = packetData.getString("type");
                        InetAddress packetAddress = packet.getAddress();

                        if (type.equals("USERNAME_VALIDATION")) {
                            mUsernameValidationUseCase.execute(packetAddress);
                        } else if (type.equals("NEW_CONNECTION")) {
                            String username = packetData.getString("username");
                            String macAddress = packetData.getString("mac_address");
                            User newUser = new User(macAddress, username, packetAddress, true);

                            mNewUserConnectionUseCase.execute(newUser);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        receivingThread.start();
    }

    public void stop() {
        if (listeningSocket != null) {
            listeningSocket.close();
        }
    }
}
