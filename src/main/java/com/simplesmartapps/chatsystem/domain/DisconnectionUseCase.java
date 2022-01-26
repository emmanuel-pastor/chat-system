package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.TCPServer;
import com.simplesmartapps.chatsystem.UDPServer;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.exception.BroadcastException;
import org.json.JSONObject;

import java.io.IOException;

import static com.simplesmartapps.chatsystem.Constants.UDP_SERVER_INPUT_PORT;

public class DisconnectionUseCase {
    private final NetworkController mNetworkController;
    private final RuntimeDataStore mRuntimeDataStore;
    private final TCPServer mTCPServer;
    private final UDPServer mUDPServer;

    @Inject
    public DisconnectionUseCase(NetworkController networkController, RuntimeDataStore runtimeDataStore, TCPServer tcpServer, UDPServer udpServer) {
        this.mNetworkController = networkController;
        this.mRuntimeDataStore = runtimeDataStore;
        this.mTCPServer = tcpServer;
        this.mUDPServer = udpServer;
    }

    public void execute() {
        try {
            mNetworkController.sendBroadcast(createDisconnectionRequest(), UDP_SERVER_INPUT_PORT);
            mRuntimeDataStore.clearKnownUsers();
            mRuntimeDataStore.readOpenedSockets().forEach((id, socket) -> {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            mRuntimeDataStore.clearOpenSockets();
        } catch (BroadcastException e) {
            e.printStackTrace();
        }
        mTCPServer.stop();
        mUDPServer.stop();
    }

    private JSONObject createDisconnectionRequest() {
        return new JSONObject().put("type", "DISCONNECTION").put("mac_address", mNetworkController.getMacAddress());
    }
}
