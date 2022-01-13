package com.simplesmartapps.chatsystem.domain.udp_server_use_case;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

public class UsernameValidationUseCase {
    private final NetworkController mNetworkController;
    private final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public UsernameValidationUseCase(NetworkController networkController, RuntimeDataStore runtimeDataStore) {
        this.mNetworkController = networkController;
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public void execute(InetAddress address) {
        JSONObject response = createUsernameValidationResponse();

        try {
            mNetworkController.sendUDP(response, address, 60418);
        } catch (IOException e) {
            System.out.println("Could not send username validation response");
            e.printStackTrace();
        }
    }

    private JSONObject createUsernameValidationResponse() {
        String macAddress = mNetworkController.getMacAddress();
        String username = mRuntimeDataStore.readUsername();
        return new JSONObject().put("username", username).put("mac_address", macAddress);
    }
}
