package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.UDPServer;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.exception.BroadcastException;
import com.simplesmartapps.chatsystem.domain.exception.ConnectionException;
import com.simplesmartapps.chatsystem.domain.exception.SelectUsernameException;
import org.json.JSONObject;

import static com.simplesmartapps.chatsystem.Constants.UDP_SERVER_INPUT_PORT;

public class ConnectionUseCase {
    private final NetworkController mNetworkController;
    private final UDPServer mUdpServer;
    private final SelectUsernameUseCase mSelectUsernameUseCase;

    @Inject
    public ConnectionUseCase(NetworkController mNetworkController, UDPServer mUdpServer, SelectUsernameUseCase mSelectUsernameUseCase) {
        this.mNetworkController = mNetworkController;
        this.mUdpServer = mUdpServer;
        this.mSelectUsernameUseCase = mSelectUsernameUseCase;
    }

    public boolean execute(String usernameCandidate) throws SelectUsernameException, ConnectionException {
        boolean isUsernameValid = mSelectUsernameUseCase.execute(usernameCandidate);
        if (isUsernameValid) {
            try {
                mNetworkController.sendBroadcast(createNewConnectionRequest(usernameCandidate), UDP_SERVER_INPUT_PORT);
            } catch (BroadcastException e) {
                throw new ConnectionException("Could not send connection broadcast", e);
            }
            mUdpServer.run();
        }
        return isUsernameValid;
    }

    private JSONObject createNewConnectionRequest(String username) {
        String macAddress = mNetworkController.getMacAddress();
        return new JSONObject().put("type", "NEW_CONNECTION").put("username", username).put("mac_address", macAddress);
    }
}
