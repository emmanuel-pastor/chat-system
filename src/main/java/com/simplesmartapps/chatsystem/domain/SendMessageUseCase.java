package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.TCPConnectionHandler;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.domain.exception.SendMessageException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import static com.simplesmartapps.chatsystem.Constants.TCP_SERVER_INPUT_PORT;

public class SendMessageUseCase {
    private final RuntimeDataStore mRuntimeDataStore;
    private final NetworkController mNetworkController;

    @Inject
    public SendMessageUseCase(RuntimeDataStore runtimeDataStore, NetworkController networkController) {
        this.mRuntimeDataStore = runtimeDataStore;
        this.mNetworkController = networkController;
    }

    public void execute(String message, User destinationUser) throws SendMessageException {
        Map<String, Socket> openedSockets = mRuntimeDataStore.readOpenedSockets();
        String destinationUserId = destinationUser.macAddress();
        boolean isSocketOpened = openedSockets.containsKey(destinationUserId);
        JSONObject jsonMessage = createJSONMessage(message);

        if (isSocketOpened) {
            try {
                sendMessage(jsonMessage, openedSockets.get(destinationUserId));
                //TODO: Add message to DB
            } catch (IOException e) {
                mRuntimeDataStore.removeOpenSocket(destinationUserId);
                mRuntimeDataStore.setUserConnectionStatus(destinationUserId, false);
                throw new SendMessageException("Could not send message through previously opened socket", e);
            }
        } else {
            try {
                Socket socket = new Socket(destinationUser.ipAddress(), TCP_SERVER_INPUT_PORT);
                sendMessage(jsonMessage, openedSockets.get(destinationUserId));

                //TODO: Add message to DB
                mRuntimeDataStore.addOpenSocket(destinationUserId, socket);
                // Start a TCPConnectionHandler to communicate with the remote user
                ChatSystemApplication.injector.getInstance(TCPConnectionHandler.class).start(socket, destinationUserId);
            } catch (IOException e) {
                mRuntimeDataStore.setUserConnectionStatus(destinationUserId, false);
                throw new SendMessageException("Could not open socket or send message through new socket", e);
            }
        }
    }

    private JSONObject createJSONMessage(String message) {
        return new JSONObject()
                .put("type", "TEXT_MESSAGE")
                .put("mac_address", mNetworkController.getMacAddress())
                .put("username", mRuntimeDataStore.readUsername())
                .put("timestamp", new Date().getTime())
                .put("content", message);
    }

    private void sendMessage(JSONObject jsonMessage, Socket socket) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(jsonMessage.toString());
    }
}
