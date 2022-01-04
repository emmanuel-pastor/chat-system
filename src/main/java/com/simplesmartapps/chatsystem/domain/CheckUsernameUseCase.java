package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

public class CheckUsernameUseCase {
    private final NetworkController mNetworkController;

    @Inject
    public CheckUsernameUseCase(NetworkController mNetworkController) {
        this.mNetworkController = mNetworkController;
    }

    public void execute(JSONObject usernameJsonRequest, InetAddress inetAddressToSendResponse) throws IOException {
        JSONObject usernameJsonResponse = new JSONObject();
        boolean isValid = !ChatSystemApplication.username.equals(usernameJsonRequest.getString("username"));
        usernameJsonResponse.put("is_valid", isValid);

        mNetworkController.sendUDP(usernameJsonResponse, inetAddressToSendResponse, 60418);
    }
}
