package com.simplesmartapps.chatsystem.presentation.messaging;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.data.remote.exception.BroadcastException;
import com.simplesmartapps.chatsystem.domain.*;
import com.simplesmartapps.chatsystem.domain.exception.SelectUsernameException;
import com.simplesmartapps.chatsystem.domain.exception.SendMessageException;
import com.simplesmartapps.chatsystem.presentation.network_listing.NetworkListingPage;
import com.simplesmartapps.chatsystem.presentation.util.NavigationUtil;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;

import java.sql.SQLException;

import static com.simplesmartapps.chatsystem.Constants.USERNAME_MAX_LENGTH;
import static com.simplesmartapps.chatsystem.presentation.util.ViewState.*;

public class MessagingViewModel {
    private final SendMessageUseCase mSendMessageUseCase;
    private final ListUserMessagesUseCase mListUserMessagesUseCase;
    private final ListLatestMessagesUseCase mListLatestMessagesUseCase;
    private final ChangeUsernameUseCase mChangeUsernameUseCase;
    private final DisconnectionUseCase mDisconnectionUseCase;
    public ObservableMap<String, User> mKnownUsers;
    public ObservableList<Message> mLatestMessages = FXCollections.observableArrayList();
    public ObservableProperty<User> mSelectedUser = new ObservableProperty<>(null);
    public ObservableProperty<ObservableList<Message>> mMessages = new ObservableProperty<>(null);
    public ObservableProperty<String> mUsername = new ObservableProperty<>("");
    public ObservableProperty<Boolean> mIsUsernameValid = new ObservableProperty<>(true);
    public ObservableProperty<ViewState> mMessagingSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<ViewState> mUsernameEditionState = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mMessagingErrorText = new ObservableProperty<>("");
    public ObservableProperty<String> mUsernameEditionErrorText = new ObservableProperty<>("");

    @Inject
    public MessagingViewModel(RuntimeDataStore runtimeDataStore, SendMessageUseCase mSendMessageUseCase, ListUserMessagesUseCase listUserMessagesUseCase, ListLatestMessagesUseCase listLatestMessagesUseCase, ChangeUsernameUseCase changeUsernameUseCase, DisconnectionUseCase disconnectionUseCase, ListUsersUseCase listUsersUseCase) {
        this.mSendMessageUseCase = mSendMessageUseCase;
        this.mListUserMessagesUseCase = listUserMessagesUseCase;
        this.mListLatestMessagesUseCase = listLatestMessagesUseCase;
        this.mChangeUsernameUseCase = changeUsernameUseCase;
        this.mDisconnectionUseCase = disconnectionUseCase;
        mKnownUsers = listUsersUseCase.execute();
        loadLatestMessages();
        mKnownUsers.addListener(knownUsersListener());
        mUsername.setValue(runtimeDataStore.readUsername());
    }

    private MapChangeListener<? super String, ? super User> knownUsersListener() {
        return change -> {
            User userAdded = change.getValueAdded();
            if (mSelectedUser.getValue() != null && userAdded != null && mSelectedUser.getValue().macAddress().equals(userAdded.macAddress())) {
                mSelectedUser.setValue(userAdded);
            }
        };
    }

    private void loadLatestMessages() {
        try {
            mLatestMessages = mListLatestMessagesUseCase.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            mMessagingErrorText.setValue("Could not load the latest messages");
            mMessagingSate.setValue(ERROR);
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
            mMessagingSate.setValue(ViewState.READY);
        });

        task.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            exception.printStackTrace();
            mMessagingErrorText.setValue("Could not load the list of messages");
            mMessagingSate.setValue(ERROR);
        });

        new Thread(task).start();
    }

    public void onSendButtonClicked(String message) {
        sendMessage(message);
    }

    public void onMessageTextFieldEnterKeyPressed(String message) {
        sendMessage(message);
    }

    private void sendMessage(String message) {
        if (message.trim().isBlank()) return;
        if (mSelectedUser.getValue() != null) {
            mMessagingSate.setValue(ViewState.LOADING);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    mSendMessageUseCase.execute(message.trim(), mSelectedUser.getValue());
                    return null;
                }
            };

            task.setOnSucceeded(event -> mMessagingSate.setValue(ViewState.READY));

            task.setOnFailed(event -> {
                Throwable exception = event.getSource().getException();
                if (exception.getClass().equals(SendMessageException.class)) {
                    mMessagingErrorText.setValue("Could not send the message, try again later");
                } else {
                    mMessagingErrorText.setValue("An unexpected error occurred");
                    exception.printStackTrace();
                }
                mMessagingSate.setValue(ViewState.ERROR);
            });

            new Thread(task).start();
        }
    }

    public void onValidateUsernameButtonClicked(String usernameCandidate) {
        checkUsernameValidity(usernameCandidate);
    }

    public void onUsernameTextFieldEnterKeyPressed(String usernameCandidate) {
        checkUsernameValidity(usernameCandidate);
    }

    private void checkUsernameValidity(String username) {
        if (username.equals(mUsername.getValue())) {
            mUsernameEditionState.setValue(READY);
            return;
        }
        mUsernameEditionState.setValue(LOADING);
        if (username.isBlank()) {
            mUsernameEditionErrorText.setValue("Username should not be empty");
            mUsernameEditionState.setValue(ERROR);
            return;
        } else if (username.length() > USERNAME_MAX_LENGTH) {
            mUsernameEditionErrorText.setValue("Username should be shorter than " + USERNAME_MAX_LENGTH + " characters");
            mUsernameEditionState.setValue(ERROR);
            return;
        }
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return mChangeUsernameUseCase.execute(username.trim());
            }
        };

        task.setOnSucceeded(event -> {
            boolean isValid = (boolean) event.getSource().getValue();
            if (isValid) {
                mIsUsernameValid.setValue(true);
                mUsername.setValue(username);
                mUsernameEditionState.setValue(READY);
            } else {
                mIsUsernameValid.setValue(false);
                mUsernameEditionErrorText.setValue("This username is already taken");
                mUsernameEditionState.setValue(ERROR);
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            if (SelectUsernameException.class.equals(exception.getClass())) {
                mUsernameEditionErrorText.setValue("Could not check username validity");
            } else if (BroadcastException.class.equals(exception.getClass())) {
                mUsernameEditionErrorText.setValue("Could not communicate your new username to the other users");
            } else {
                mUsernameEditionErrorText.setValue("An unexpected error occurred");
                exception.printStackTrace();
            }
            mUsernameEditionState.setValue(ERROR);
        });

        new Thread(task).start();
    }

    public void onUsernameTextFieldEscapeKeyPressed() {
        mUsernameEditionState.setValue(READY);
    }

    public void onDisconnectionButtonClicked() {
        mDisconnectionUseCase.execute();
        cleanUp();
        NavigationUtil.navigateTo(NetworkListingPage.class);
    }

    private void cleanUp() {
        mKnownUsers.removeListener(knownUsersListener());
        mSelectedUser.clearObservers();
        mMessages.clearObservers();
        mUsername.clearObservers();
        mIsUsernameValid.clearObservers();
        mMessagingSate.clearObservers();
        mUsernameEditionState.clearObservers();
        mMessagingErrorText.clearObservers();
        mUsernameEditionErrorText.clearObservers();
    }
}

