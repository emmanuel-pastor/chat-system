package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.exception.SelectUsernameException;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastResponse;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.simplesmartapps.chatsystem.Constants.UDP_SERVER_INPUT_PORT;
import static com.simplesmartapps.chatsystem.Constants.USERNAME_CHECK_TIMEOUT;

public class SelectUsernameUseCase {
    private final NetworkController mNetworkController;
    private final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public SelectUsernameUseCase(NetworkController networkController, RuntimeDataStore runtimeDataStore) {
        this.mNetworkController = networkController;
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public boolean execute(String usernameCandidate) throws SelectUsernameException {
        try {
            List<BroadcastResponse> responseList = mNetworkController.sendBroadcastWithMultipleResponses(createUsernameValidationRequest(), UDP_SERVER_INPUT_PORT, USERNAME_CHECK_TIMEOUT);

            Set<User> usersSet = responseList.stream().map(this::userFromBroadcastResponse).collect(Collectors.toSet());
            boolean isUsernameValid = usersSet.stream().noneMatch(connectedUser -> connectedUser.username().equals(usernameCandidate));

            if (isUsernameValid) {
                mRuntimeDataStore.writeUsername(usernameCandidate);
                mRuntimeDataStore.writeUsersSet(usersSet);
            }

            return isUsernameValid;
        } catch (Exception e) {
            throw new SelectUsernameException("A network error occurred while trying to validate the username", e);
        }
    }

    private JSONObject createUsernameValidationRequest() {
        return new JSONObject().put("type", "USERNAME_VALIDATION");
    }

    private User userFromBroadcastResponse(BroadcastResponse broadcastResponse) {
        JSONObject jsonResponse = broadcastResponse.json();
        String macAddress = jsonResponse.getString("mac_address");
        String username = jsonResponse.getString("username");

        InetAddress ipAddress = broadcastResponse.address();

        return new User(macAddress, username, ipAddress, true);
    }
}
