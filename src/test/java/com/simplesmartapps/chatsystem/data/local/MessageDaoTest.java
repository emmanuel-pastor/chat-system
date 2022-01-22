package com.simplesmartapps.chatsystem.data.local;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.simplesmartapps.chatsystem.CoreTestDIModule;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.MessageType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Date;

public class MessageDaoTest {
    private static Injector injector;
    private DatabaseController mDbController;
    private MessageDao mMessageDao;

    @BeforeAll
    static void setUpBeforeAll() {
        injector = Guice.createInjector(new CoreTestDIModule());
    }

    @BeforeEach
    void setUpBeforeEach() {
        mDbController = injector.getInstance(DatabaseController.class);
        mMessageDao = injector.getInstance(MessageDao.class);
    }

    @Test
    void shouldInsertMessageInDb() throws SQLException {
        Message message = new Message(0,
                "50-E0-85-E4-07-19",
                MessageType.TEXT_MESSAGE,
                new Date().getTime(),
                false,
                "test message");

        mMessageDao.insertMessage(message);
    }

    @AfterEach
    void cleanUpAfterEach() {
        mDbController.closeConnection();
    }
}
