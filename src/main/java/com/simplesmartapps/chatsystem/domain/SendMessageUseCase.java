package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.TCPConnectionHandler;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.MessageType;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.domain.exception.SendMessageException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import static com.simplesmartapps.chatsystem.Constants.TCP_SERVER_INPUT_PORT;

public class SendMessageUseCase {
    private final RuntimeDataStore mRuntimeDataStore;
    private final NetworkController mNetworkController;
    private final MessageDao mMessageDao;

    @Inject
    public SendMessageUseCase(RuntimeDataStore runtimeDataStore, NetworkController networkController, MessageDao messageDao) {
        this.mRuntimeDataStore = runtimeDataStore;
        this.mNetworkController = networkController;
        this.mMessageDao = messageDao;
    }

    public void execute(String messageContent, User destinationUser) throws SendMessageException {
        Map<String, Socket> openedSockets = mRuntimeDataStore.readOpenedSockets();
        String destinationUserId = destinationUser.macAddress();
        boolean isSocketOpened = openedSockets.containsKey(destinationUserId);
        JSONObject jsonMessage = createJSONMessage(messageContent);
        Message message = createMessage(destinationUserId, messageContent);

        if (isSocketOpened) {
            try {
                sendMessage(jsonMessage, openedSockets.get(destinationUserId));
                mMessageDao.insertMessage(message);
            } catch (IOException e) {
                mRuntimeDataStore.removeOpenSocket(destinationUserId);
                mRuntimeDataStore.setUserConnectionStatus(destinationUserId, false);
                throw new SendMessageException("Could not send message through previously opened socket", e);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Socket socket = new Socket(destinationUser.ipAddress(), TCP_SERVER_INPUT_PORT);
                sendMessage(jsonMessage, openedSockets.get(destinationUserId));

                mMessageDao.insertMessage(message);
                mRuntimeDataStore.addOpenSocket(destinationUserId, socket);
                // Start a TCPConnectionHandler to communicate with the remote user
                ChatSystemApplication.injector.getInstance(TCPConnectionHandler.class).start(socket, destinationUserId);
            } catch (IOException e) {
                mRuntimeDataStore.setUserConnectionStatus(destinationUserId, false);
                throw new SendMessageException("Could not open socket or send message through new socket", e);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject createJSONMessage(String content) {
        return new JSONObject()
                .put("type", "TEXT_MESSAGE")
                .put("mac_address", mNetworkController.getMacAddress())
                .put("username", mRuntimeDataStore.readUsername())
                .put("timestamp", new Date().getTime())
                .put("content", content);
    }

    private Message createMessage(String destinationUserId, String content) {
        return new Message(
                0,
                destinationUserId,
                MessageType.TEXT_MESSAGE,
                new Date().getTime(),
                false,
                content
        );
    }

    private void sendMessage(JSONObject jsonMessage, Socket socket) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(jsonMessage.toString());
    }
}
