package com.simplesmartapps.chatsystem.domain.udp_server_use_case;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NewUsernameUseCase {
    final NetworkController mNetworkController;
    final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public NewUsernameUseCase(NetworkController networkController, RuntimeDataStore runtimeDataStore) {
        this.mNetworkController = networkController;
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public void execute(User modifiedUser) {
        if (!modifiedUser.macAddress().equals(mNetworkController.getMacAddress())) {
            Set<User> oldUsers = mRuntimeDataStore.readUsersSet();
            List<User> modifiedUsersList = oldUsers.stream().filter(user -> !user.macAddress().equals(modifiedUser.macAddress())).collect(Collectors.toList());
            modifiedUsersList.add(modifiedUser);
            mRuntimeDataStore.addAllUsers(new HashSet<>(modifiedUsersList));
        }
    }
}
