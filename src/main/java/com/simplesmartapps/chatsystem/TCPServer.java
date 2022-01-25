package com.simplesmartapps.chatsystem;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.MessageType;
import com.simplesmartapps.chatsystem.data.local.model.User;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static com.simplesmartapps.chatsystem.Constants.TCP_SERVER_INPUT_PORT;

public class TCPServer {
    private final RuntimeDataStore mRuntimeDataStore;
    private final MessageDao mMessageDao;
    private ServerSocket mServerSocket;

    @Inject
    public TCPServer(RuntimeDataStore runtimeDataStore, MessageDao messageDao) {
        this.mRuntimeDataStore = runtimeDataStore;
        this.mMessageDao = messageDao;
    }

    public void start() throws IOException {
        mServerSocket = new ServerSocket(TCP_SERVER_INPUT_PORT);

        Thread serverThread = new Thread(() -> {
            try {
                while (true) {
                    Socket clientSocket = mServerSocket.accept();

                    JSONObject firstMessage = readFirstMessage(clientSocket);
                    String remoteUserId = firstMessage.getString("mac_address");
                    String remoteUsername = firstMessage.getString("username");
                    MessageType type = MessageType.valueOf(firstMessage.getString("type"));
                    long timestamp = firstMessage.getLong("timestamp");
                    InetAddress sourceIpAddress = clientSocket.getInetAddress();
                    String messageContent = firstMessage.getString("content");

                    mRuntimeDataStore.addOpenSocket(remoteUserId, clientSocket);
                    updateUsersSetIfNeeded(remoteUserId, remoteUsername, sourceIpAddress);
                    // Start a TCPConnectionHandler to communicate with the remote user
                    ChatSystemApplication.injector.getInstance(TCPConnectionHandler.class).start(clientSocket, remoteUserId);
                    Message message = new Message(0, remoteUserId, type, timestamp, true, messageContent);
                    try {
                        mMessageDao.insertMessage(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    private JSONObject readFirstMessage(Socket clientSocket) throws IOException {
        JSONTokener tokener = new JSONTokener(clientSocket.getInputStream());
        return new JSONObject(tokener);
    }

    private void updateUsersSetIfNeeded(String userId, String username, InetAddress ipAddress) {
        Map<String, User> knownUsers = mRuntimeDataStore.readKnownUsers();
        User existingUser = knownUsers.get(userId);

        if (existingUser != null) {
            if (!existingUser.isConnected()) {
                mRuntimeDataStore.setUserConnectionStatus(userId, true);
            }
        } else {
            User newUser = new User(userId, username, ipAddress, true);
            mRuntimeDataStore.addAllUsers(Collections.singletonMap(userId, newUser));
        }
    }

    public void stop() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}