package com.simplesmartapps.chatsystem.data.local;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {
    public final Connection mConnection;

    @Inject
    public DatabaseController(Connection connection) throws SQLException {
        this.mConnection = connection;

        createTables();
    }

    private void createTables() throws SQLException {
        Statement statement = mConnection.createStatement();
        String messagesTableSql = "CREATE TABLE if not exists \"messages\" (\n" +
                "  \"id\" INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  \"type\" text NOT NULL,\n" +
                "  \"remote_user_id\" text NOT NULL,\n" +
                "  \"timestamp\" integer NOT NULL,\n" +
                "  \"is_incoming\" boolean NOT NULL,\n" +
                "  \"content\" text boolean NOT NULL,\n" +
                ");";

        statement.execute(messagesTableSql);
        statement.close();
    }

    public void closeConnection() {
        try {
            mConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
