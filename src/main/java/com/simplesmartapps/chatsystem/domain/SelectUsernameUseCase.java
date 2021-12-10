package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.BroadcastException;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.SelectUsernameException;
import org.json.JSONObject;

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
            List<JSONObject> responseList = mNetworkController.sendBroadcastWithMultipleResponses(toJsonObjectRequest(username), USERNAME_CHECK_TIMEOUT);
            return !responseList.stream().map(this::fromJsonObjectResponse).toList().contains(false);
        } catch (BroadcastException e) {
            throw new SelectUsernameException(e.getMessage());
        }
    }

    private JSONObject toJsonObjectRequest(String username) {
        JSONObject baseObject = new JSONObject();
        baseObject.put("username", username);
        return baseObject;
    }

    private Boolean fromJsonObjectResponse(JSONObject response) {
        return response.getBoolean("is_valid");
    }
}
