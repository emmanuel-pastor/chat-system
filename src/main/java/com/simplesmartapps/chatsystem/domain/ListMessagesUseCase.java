package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class ListMessagesUseCase {
    final private MessageDao mMessagingDao;

    @Inject
    public ListMessagesUseCase(MessageDao mMessagingDao) {
        this.mMessagingDao = mMessagingDao;
    }

    public ObservableList<Message> execute(String remoteUserId) throws SQLException {
        return mMessagingDao.getMessagesByUserId(remoteUserId);
    }
}
