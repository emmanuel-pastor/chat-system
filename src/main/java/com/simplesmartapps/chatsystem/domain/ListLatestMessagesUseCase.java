package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class ListLatestMessagesUseCase {
    private final MessageDao mMessageDao;

    @Inject
    public ListLatestMessagesUseCase(MessageDao mMessageDao) {
        this.mMessageDao = mMessageDao;
    }

    public ObservableList<Message> execute() throws SQLException {
        return mMessageDao.getLatestMessages();
    }
}
