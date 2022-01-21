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

public class UDPServer implements Runnable {
    private final UsernameValidationUseCase mUsernameValidationUseCase;
    private final NewUserConnectionUseCase mNewUserConnectionUseCase;

    @Inject
    public UDPServer(UsernameValidationUseCase usernameValidationUseCase, NewUserConnectionUseCase newUserConnectionUseCase) {
        this.mUsernameValidationUseCase = usernameValidationUseCase;
        this.mNewUserConnectionUseCase = newUserConnectionUseCase;
    }

    @Override
    public void run() {
        Thread receivingThread = new Thread("udp_server_thread") {
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
                                mUsernameValidationUseCase.execute(packetAddress);
                            } else if (type.equals("NEW_CONNECTION")) {
                                String username = packetData.getString("username");
                                String macAddress = packetData.getString("mac_address");
                                User newUser = new User(macAddress, username, packetAddress, true);

                                mNewUserConnectionUseCase.execute(newUser);
                            }
                        } catch (IOException e) {
                            System.out.println("An error occurred with receiving or sending data in the UDP server");
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
}
