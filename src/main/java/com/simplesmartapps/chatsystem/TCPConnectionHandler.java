package com.simplesmartapps.chatsystem;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.MessageType;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class TCPConnectionHandler {
    private final RuntimeDataStore mRuntimeDataStore;
    private final MessageDao mMessageDao;

    @Inject
    public TCPConnectionHandler(RuntimeDataStore runtimeDataStore, MessageDao messageDao) {
        this.mRuntimeDataStore = runtimeDataStore;
        this.mMessageDao = messageDao;
    }

    public void start(Socket socket, String remoteUserId) {
        Thread handlerThread = new Thread(() -> {
            try {
                JSONTokener tokener = new JSONTokener(socket.getInputStream());
                while (!tokener.end()) {
                    JSONObject json = new JSONObject(tokener);
                    String remoteUsername = json.getString("username");
                    MessageType type = MessageType.valueOf(json.getString("type"));
                    long timestamp = json.getLong("timestamp");
                    String content = json.getString("content");

                    System.out.println(remoteUsername + " says: " + content);
                    Message message = new Message(0, remoteUserId, type, timestamp, true, content);
                    try {
                        mMessageDao.insertMessage(message);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException | JSONException e1) {
                try {
                    mRuntimeDataStore.removeOpenSocket(remoteUserId);
                    mRuntimeDataStore.setUserConnectionStatus(remoteUserId, false);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        handlerThread.start();
    }
}
