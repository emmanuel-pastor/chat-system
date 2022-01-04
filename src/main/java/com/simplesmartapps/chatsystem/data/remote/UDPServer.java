package com.simplesmartapps.chatsystem.data.remote;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.util.JsonUtil;
import com.simplesmartapps.chatsystem.domain.CheckUsernameUseCase;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer implements Runnable {
    private final DatagramSocket mUsernameValidationSocket;
    private final CheckUsernameUseCase mCheckUsernameUseCase;
    private final byte[] buffer = new byte[1024];

    @Inject
    public UDPServer(CheckUsernameUseCase checkUsernameUseCase) throws SocketException {
        this.mUsernameValidationSocket = new DatagramSocket(4445);
        this.mCheckUsernameUseCase = checkUsernameUseCase;
    }

    @Override
    public void run() {
        Thread receive = new Thread("receive_thread") {
            public void run() {
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        mUsernameValidationSocket.receive(packet);
                        mCheckUsernameUseCase.execute(JsonUtil.fromByteToJson(packet.getData()), packet.getAddress());
                        System.out.println("Packet sent to" + packet.getAddress());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        receive.start();
    }
}
