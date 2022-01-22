package com.simplesmartapps.chatsystem.data.local;

import com.google.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseController {
    private final Connection mConnection;
    private final Statement mStatement;

    @Inject
    public DatabaseController(Connection connection) throws SQLException {
        this.mConnection = connection;
        this.mStatement = mConnection.createStatement();

        createTables();
    }

    private void createTables() throws SQLException {
        String usersTableSql = "CREATE TABLE if not exists \"users\" (\n" +
                "  \"id\" varchar PRIMARY KEY,\n" +
                "  \"last_username\" varchar\n" +
                ");\n";
        String messagesTableSql = "CREATE TABLE if not exists \"messages\" (\n" +
                "  \"id\" SERIAL PRIMARY KEY,\n" +
                "  \"remote_user_id\" varchar,\n" +
                "  \"type\" enum,\n" +
                "  \"timestamp\" timestamp,\n" +
                "  \"content\" text,\n" +
                "  FOREIGN KEY (\"remote_user_id\") REFERENCES \"users\" (\"id\")\n" +
                ");";

        mStatement.execute(usersTableSql);
        mStatement.execute(messagesTableSql);
    }

    public void closeConnection() {
        try {
            mStatement.close();
            mConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
