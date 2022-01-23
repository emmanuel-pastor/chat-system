package com.simplesmartapps.chatsystem.data.local.dao;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.DatabaseController;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.MessageType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    private final DatabaseController mDbController;
    private final ObservableList<Message> mMessagesList = FXCollections.observableArrayList();
    private String currentlyObservedUserId;

    @Inject
    public MessageDao(DatabaseController dbController) {
        this.mDbController = dbController;
    }

    public void insertMessage(Message message) throws SQLException {
        String insertStatement = "INSERT INTO messages (type, remote_user_id, timestamp, is_incoming, content) VALUES (?,?,?,?,?)";
        PreparedStatement statement = mDbController.mConnection.prepareStatement(insertStatement);

        statement.setString(1, message.type().getValue());
        statement.setString(2, message.remoteUserId());
        statement.setLong(3, message.timestamp());
        statement.setBoolean(4, message.isIncoming());
        statement.setString(5, message.content());

        statement.execute();
        statement.close();

        if (message.remoteUserId().equals(currentlyObservedUserId)) {
            getMessagesByUserIdExecution(currentlyObservedUserId);
        }
    }

    public ObservableList<Message> getMessagesByUserId(String remoteUserId) throws SQLException {
        if (remoteUserId == null) return mMessagesList;
        currentlyObservedUserId = remoteUserId;
        getMessagesByUserIdExecution(remoteUserId);
        return mMessagesList;
    }

    private void getMessagesByUserIdExecution(String remoteUserId) throws SQLException {
        if (remoteUserId == null) return;

        String selectStatement = "SELECT * FROM messages WHERE messages.remote_user_id = ? ORDER BY timestamp";
        PreparedStatement statement = mDbController.mConnection.prepareStatement(selectStatement);

        statement.setString(1, remoteUserId);

        ResultSet resultSet = statement.executeQuery();
        List<Message> messages = new ArrayList<>();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            MessageType type = MessageType.valueOf(resultSet.getString("type"));
            long timestamp = resultSet.getLong("timestamp");
            boolean isIncoming = resultSet.getBoolean("is_incoming");
            String content = resultSet.getString("content");
            Message message = new Message(id, remoteUserId, type, timestamp, isIncoming, content);

            messages.add(message);
        }

        statement.close();

        Platform.runLater(() -> mMessagesList.setAll(messages));
    }
}
