package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.SelectUsernameException;
import com.simplesmartapps.chatsystem.data.local.model.User;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;

import static com.simplesmartapps.chatsystem.Constants.USERNAME_CHECK_TIMEOUT;

public class SelectUsernameUseCase {
    private final NetworkController mNetworkController;

    @Inject
    public SelectUsernameUseCase(NetworkController mNetworkController) {
        this.mNetworkController = mNetworkController;
    }

    public boolean execute(String username) throws SelectUsernameException {
        try {
            List<JSONObject> responseList = mNetworkController.sendBroadcastWithMultipleResponses(toJsonObjectRequest(), USERNAME_CHECK_TIMEOUT);
            return !(responseList.stream().map(this::fromJsonObjectResponse).filter(user -> user.username().equals(ChatSystemApplication.username)).toArray().length > 0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SelectUsernameException("A network error occurred while trying to validate the username", e);
        }
    }

    private JSONObject toJsonObjectRequest() {
        JSONObject baseObject = new JSONObject();
        baseObject.put("type", "USERNAME_VALIDATION");
        return baseObject;
    }

    private User fromJsonObjectResponse(JSONObject response) {
        try {
            String macAddress = response.getString("mac_address");
            String username = response.getString("username");
            InetAddress ipAddress = InetAddress.getByName(response.getString("ip_address"));
            return new User(macAddress, username, true, ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
