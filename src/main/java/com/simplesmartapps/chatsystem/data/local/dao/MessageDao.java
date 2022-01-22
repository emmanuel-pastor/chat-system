package com.simplesmartapps.chatsystem.data.local.dao;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.DatabaseController;
import com.simplesmartapps.chatsystem.data.local.model.Message;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDao {
    private final DatabaseController mDbController;

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
    }
}
