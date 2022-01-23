package com.simplesmartapps.chatsystem.presentation.messaging;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.domain.ListUsersUseCase;
import com.simplesmartapps.chatsystem.domain.SendMessageUseCase;
import com.simplesmartapps.chatsystem.domain.exception.SendMessageException;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

public class MessagingViewModel {
    private final SendMessageUseCase mSendMessageUseCase;
    public ObservableSet<User> mUsersSet;
    public ObservableProperty<User> mSelectedUser = new ObservableProperty<>(null);
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public MessagingViewModel(SendMessageUseCase mSendMessageUseCase, ListUsersUseCase listUsersUseCase) {
        this.mSendMessageUseCase = mSendMessageUseCase;
        mUsersSet = listUsersUseCase.execute();
        mUsersSet.addListener((SetChangeListener<User>) change -> {
            User userAdded = change.getElementAdded();
            if (mSelectedUser.getValue() != null && userAdded != null && mSelectedUser.getValue().macAddress().equals(userAdded.macAddress())) {
                mSelectedUser.setValue(userAdded);
            }
        });
    }

    public void onListItemClicked(User selectedUser) {
        mSelectedUser.setValue(selectedUser);
    }

    public void onSendButtonClicked(String message) {
        sendMessage(message);
    }

    public void onEnterKeyPressed(String message) {
        sendMessage(message);
    }

    private void sendMessage(String message) {
        if (mSelectedUser.getValue() != null) {
            try {
                mSate.setValue(ViewState.LOADING);
                mSendMessageUseCase.execute(message, mSelectedUser.getValue());
                mSate.setValue(ViewState.READY);
            } catch (SendMessageException e) {
                e.printStackTrace();
                mSate.setValue(ViewState.ERROR);
            }
        }
    }
}

