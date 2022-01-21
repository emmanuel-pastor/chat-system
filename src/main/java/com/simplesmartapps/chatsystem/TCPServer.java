package com.simplesmartapps.chatsystem;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.simplesmartapps.chatsystem.Constants.TCP_SERVER_INPUT_PORT;

public class TCPServer {
    private final RuntimeDataStore mRuntimeDataStore;
    private ServerSocket mServerSocket;

    @Inject
    public TCPServer(RuntimeDataStore runtimeDataStore) {
        this.mRuntimeDataStore = runtimeDataStore;
    }

    public void start() throws IOException {
        mServerSocket = new ServerSocket(TCP_SERVER_INPUT_PORT);

        Thread serverThread = new Thread("tcp_server_thread") {
            public void run() {
                try {
                    while (true) {
                        Socket clientSocket = mServerSocket.accept();

                        JSONObject firstMessage = readFirstMessage(clientSocket);
                        String userId = firstMessage.getString("mac_address");
                        String remoteUsername = firstMessage.getString("username");
                        InetAddress sourceIpAddress = clientSocket.getInetAddress();
                        String messageContent = firstMessage.getString("content");

                        mRuntimeDataStore.addOpenSocket(userId, clientSocket);
                        updateUsersSetIfNeeded(userId, remoteUsername, sourceIpAddress);
                        // Start a TCPConnectionHandler to communicate with the remote user
                        ChatSystemApplication.injector.getInstance(TCPConnectionHandler.class).start(clientSocket, userId);
                        //TODO: Add message to DB
                        System.out.println(remoteUsername + " says: " + messageContent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread.start();
    }

    private JSONObject readFirstMessage(Socket clientSocket) throws IOException {
        JSONTokener tokener = new JSONTokener(clientSocket.getInputStream());
        return new JSONObject(tokener);
    }

    private void updateUsersSetIfNeeded(String userId, String username, InetAddress ipAddress) {
        Set<User> usersSet = mRuntimeDataStore.readUsersSet();
        Optional<User> optionalUserInSet = usersSet.stream().filter(user -> user.macAddress().equals(userId)).findFirst();

        if (optionalUserInSet.isPresent()) {
            User existingUser = optionalUserInSet.get();
            if (!existingUser.isConnected()) {
                mRuntimeDataStore.setUserConnectionStatus(userId, true);
            }
        } else {
            User newUser = new User(userId, username, ipAddress, true);
            mRuntimeDataStore.addAllUsers(new HashSet<>(List.of(newUser)));
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