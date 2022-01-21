package com.simplesmartapps.chatsystem;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.Socket;

public class TCPConnectionHandler {
    private final RuntimeDataStore mRuntimeDataStore;

    @Inject
    public TCPConnectionHandler(RuntimeDataStore runtimeDataStore) {
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public void start(Socket socket, String remoteUserId) {
        Thread handlerThread = new Thread(() -> {
            try {
                JSONTokener tokener = new JSONTokener(socket.getInputStream());
                while (!tokener.end()) {
                    JSONObject json = new JSONObject(tokener);
                    String remoteUsername = json.getString("username");
                    String content = json.getString("content");
                    System.out.println(remoteUsername + " says: " + content);
                    //TODO: Add message to DB
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
