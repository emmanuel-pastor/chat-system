package com.simplesmartapps.chatsystem.presentation.username_selection;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.domain.ConnectionUseCase;
import com.simplesmartapps.chatsystem.domain.exception.ConnectionException;
import com.simplesmartapps.chatsystem.domain.exception.SelectUsernameException;
import com.simplesmartapps.chatsystem.presentation.messaging.MessagingPage;
import com.simplesmartapps.chatsystem.presentation.network_listing.NetworkListingPage;
import com.simplesmartapps.chatsystem.presentation.util.NavigationUtil;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.concurrent.Task;

import static com.simplesmartapps.chatsystem.Constants.USERNAME_MAX_LENGTH;
import static com.simplesmartapps.chatsystem.presentation.username_selection.UsernameSelectionViewModel.ValidityState.*;
import static com.simplesmartapps.chatsystem.presentation.util.ViewState.*;

public class UsernameSelectionViewModel {
    private final ConnectionUseCase mConnectionUseCase;

    public ObservableProperty<ViewState> mState = new ObservableProperty<>(READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");
    public ObservableProperty<ValidityState> mIsValid = new ObservableProperty<>(UNKNOWN);

    @Inject
    public UsernameSelectionViewModel(ConnectionUseCase mConnectionUseCase) {
        this.mConnectionUseCase = mConnectionUseCase;
    }

    public void onSubmitButtonClicked(String username) {
        checkUsernameValidity(username);
    }

    public void onEnterKeyPressed(String username) {
        checkUsernameValidity(username);
    }

    private void checkUsernameValidity(String username) {
        mState.setValue(LOADING);
        if (username.isBlank()) {
            mErrorText.setValue("Username should not be empty");
            mState.setValue(ERROR);
            return;
        } else if (username.length() > USERNAME_MAX_LENGTH) {
            mErrorText.setValue("Username should be shorter than " + USERNAME_MAX_LENGTH + " characters");
            mState.setValue(ERROR);
            return;
        }
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return mConnectionUseCase.execute(username.trim());
            }
        };

        task.setOnSucceeded(event -> {
            boolean isValid = (boolean) event.getSource().getValue();
            mState.setValue(READY);
            if (isValid) {
                mIsValid.setValue(VALID);
                cleanup();
                NavigationUtil.navigateTo(MessagingPage.class);
            } else {
                mIsValid.setValue(INVALID);
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            if (SelectUsernameException.class.equals(exception.getClass())) {
                mErrorText.setValue("Could not check username validity");
            } else if (ConnectionException.class.equals(exception.getClass())) {
                mErrorText.setValue("Could not connect to the system");
            } else {
                mErrorText.setValue("An unexpected error occurred");
                exception.printStackTrace();
            }
            mState.setValue(ERROR);
        });

        new Thread(task).start();
    }

    public void networkPageButtonCLicked() {
        cleanup();
        NavigationUtil.navigateTo(NetworkListingPage.class);
    }

    private void cleanup() {
        mState.clearObservers();
        mIsValid.clearObservers();
        mErrorText.clearObservers();
    }

    enum ValidityState {
        UNKNOWN,
        VALID,
        INVALID
    }

}
