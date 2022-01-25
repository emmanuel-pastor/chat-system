package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.exception.BroadcastException;
import com.simplesmartapps.chatsystem.domain.exception.SelectUsernameException;
import org.json.JSONObject;

import static com.simplesmartapps.chatsystem.Constants.UDP_SERVER_INPUT_PORT;

public class ChangeUsernameUseCase {
    private final NetworkController mNetworkController;
    private final SelectUsernameUseCase mSelectUsernameUseCase;

    @Inject
    public ChangeUsernameUseCase(NetworkController networkController, SelectUsernameUseCase selectUsernameUseCase) {
        this.mNetworkController = networkController;
        this.mSelectUsernameUseCase = selectUsernameUseCase;
    }

    public boolean execute(String usernameCandidate) throws SelectUsernameException, BroadcastException {
        boolean isUsernameValid = mSelectUsernameUseCase.execute(usernameCandidate);
        if (isUsernameValid) {
            JSONObject request = createModifiedUsernameRequest(usernameCandidate);
            mNetworkController.sendBroadcast(request, UDP_SERVER_INPUT_PORT);
        }
        return isUsernameValid;
    }

    private JSONObject createModifiedUsernameRequest(String username) {
        String macAddress = mNetworkController.getMacAddress();
        return new JSONObject().put("type", "NEW_USERNAME").put("username", username).put("mac_address", macAddress);
    }
}
