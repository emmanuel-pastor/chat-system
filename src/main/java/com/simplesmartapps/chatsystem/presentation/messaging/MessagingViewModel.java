package com.simplesmartapps.chatsystem.presentation.messaging;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.domain.ListLatestMessagesUseCase;
import com.simplesmartapps.chatsystem.domain.ListUserMessagesUseCase;
import com.simplesmartapps.chatsystem.domain.ListUsersUseCase;
import com.simplesmartapps.chatsystem.domain.SendMessageUseCase;
import com.simplesmartapps.chatsystem.domain.exception.SendMessageException;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;

import java.sql.SQLException;

import static com.simplesmartapps.chatsystem.presentation.util.ViewState.ERROR;

public class MessagingViewModel {
    private final SendMessageUseCase mSendMessageUseCase;
    private final ListUserMessagesUseCase mListUserMessagesUseCase;
    private final ListLatestMessagesUseCase mListLatestMessagesUseCase;
    public ObservableSet<User> mUsersSet;
    public ObservableList<Message> mLatestMessages = FXCollections.observableArrayList();
    public ObservableProperty<User> mSelectedUser = new ObservableProperty<>(null);
    public ObservableProperty<ObservableList<Message>> mMessages = new ObservableProperty<>(null);
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public MessagingViewModel(SendMessageUseCase mSendMessageUseCase, ListUserMessagesUseCase listUserMessagesUseCase, ListLatestMessagesUseCase listLatestMessagesUseCase, ListUsersUseCase listUsersUseCase) {
        this.mSendMessageUseCase = mSendMessageUseCase;
        this.mListUserMessagesUseCase = listUserMessagesUseCase;
        this.mListLatestMessagesUseCase = listLatestMessagesUseCase;
        mUsersSet = listUsersUseCase.execute();
        loadLatestMessages();
        mUsersSet.addListener((SetChangeListener<User>) change -> {
            User userAdded = change.getElementAdded();
            if (mSelectedUser.getValue() != null && userAdded != null && mSelectedUser.getValue().macAddress().equals(userAdded.macAddress())) {
                mSelectedUser.setValue(userAdded);
            }
        });
    }

    private void loadLatestMessages() {
        try {
            mLatestMessages = mListLatestMessagesUseCase.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            mErrorText.setValue("Could not load the latest messages");
            mSate.setValue(ERROR);
        }
    }

    public void onListItemClicked(User selectedUser) {
        mSelectedUser.setValue(selectedUser);
        updateMessagesList(selectedUser.macAddress());
    }

    @SuppressWarnings("unchecked")
    private void updateMessagesList(String selectedUserId) {
        Task<ObservableList<Message>> task = new Task<>() {
            @Override
            protected ObservableList<Message> call() throws Exception {
                return mListUserMessagesUseCase.execute(selectedUserId);
            }
        };

        task.setOnSucceeded(event -> {
            mMessages.setValue((ObservableList<Message>) event.getSource().getValue());
            mSate.setValue(ViewState.READY);
        });

        task.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            exception.printStackTrace();
            mErrorText.setValue("Could not load the list of messages");
            mSate.setValue(ERROR);
        });

        new Thread(task).start();
    }

    public void onSendButtonClicked(String message) {
        sendMessage(message);
    }

    public void onEnterKeyPressed(String message) {
        sendMessage(message);
    }

    private void sendMessage(String message) {
        if (message.trim().isBlank()) return;
        if (mSelectedUser.getValue() != null) {
            mSate.setValue(ViewState.LOADING);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    mSendMessageUseCase.execute(message.trim(), mSelectedUser.getValue());
                    return null;
                }
            };

            task.setOnSucceeded(event -> mSate.setValue(ViewState.READY));

            task.setOnFailed(event -> {
                Throwable exception = event.getSource().getException();
                if (exception.getClass().equals(SendMessageException.class)) {
                    mErrorText.setValue("Could not send the message, try again later");
                } else {
                    mErrorText.setValue("An unexpected error occurred");
                    exception.printStackTrace();
                }
                mSate.setValue(ViewState.ERROR);
            });

            new Thread(task).start();
        }
    }
}

