package com.simplesmartapps.chatsystem.presentation.messaging;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.domain.ListMessagesUseCase;
import com.simplesmartapps.chatsystem.domain.ListUsersUseCase;
import com.simplesmartapps.chatsystem.domain.SendMessageUseCase;
import com.simplesmartapps.chatsystem.domain.exception.SendMessageException;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;

import static com.simplesmartapps.chatsystem.presentation.util.ViewState.ERROR;

public class MessagingViewModel {
    private final SendMessageUseCase mSendMessageUseCase;
    private final ListMessagesUseCase mListMessagesUseCase;
    public ObservableSet<User> mUsersSet;
    public ObservableProperty<User> mSelectedUser = new ObservableProperty<>(null);
    public ObservableProperty<ObservableList<Message>> mMessagesList = new ObservableProperty<>(null);
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public MessagingViewModel(SendMessageUseCase mSendMessageUseCase, ListMessagesUseCase listMessagesUseCase, ListUsersUseCase listUsersUseCase) {
        this.mSendMessageUseCase = mSendMessageUseCase;
        this.mListMessagesUseCase = listMessagesUseCase;
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
        updateMessagesList(selectedUser.macAddress());
    }

    @SuppressWarnings("unchecked")
    private void updateMessagesList(String selectedUserId) {
        Task<ObservableList<Message>> task = new Task<>() {
            @Override
            protected ObservableList<Message> call() throws Exception {
                return mListMessagesUseCase.execute(selectedUserId);
            }
        };

        task.setOnSucceeded(event -> {
            mMessagesList.setValue((ObservableList<Message>) event.getSource().getValue());
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
        if (mSelectedUser.getValue() != null) {
            mSate.setValue(ViewState.LOADING);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    mSendMessageUseCase.execute(message, mSelectedUser.getValue());
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

