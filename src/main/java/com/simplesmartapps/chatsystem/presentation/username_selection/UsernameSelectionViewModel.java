package com.simplesmartapps.chatsystem.presentation.username_selection;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.domain.SelectUsernameUseCase;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.concurrent.Task;

import static com.simplesmartapps.chatsystem.presentation.username_selection.UsernameSelectionViewModel.ValidityState.*;
import static com.simplesmartapps.chatsystem.presentation.util.ViewState.*;

public class UsernameSelectionViewModel {
    private final SelectUsernameUseCase mSelectUsernameUseCase;

    public ObservableProperty<ViewState> mState = new ObservableProperty<>(READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");
    public ObservableProperty<ValidityState> mIsValid = new ObservableProperty<>(UNKNOWN);

    @Inject
    public UsernameSelectionViewModel(SelectUsernameUseCase mSelectUsernameUseCase) {
        this.mSelectUsernameUseCase = mSelectUsernameUseCase;
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
        }
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return mSelectUsernameUseCase.execute(username.trim());
            }
        };

        task.setOnSucceeded(event -> {
            boolean isValid = (boolean) event.getSource().getValue();
            mState.setValue(READY);
            if (isValid) {
                mIsValid.setValue(VALID);
            } else {
                mIsValid.setValue(INVALID);
            }
        });

        task.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            if ("SelectUsernameException".equals(exception.toString())) {
                mErrorText.setValue("Could not check username validity");
            } else {
                mErrorText.setValue("An unexpected error occurred");
            }
            mState.setValue(ERROR);
        });

        new Thread(task).start();
    }


    enum ValidityState {
        UNKNOWN,
        VALID,
        INVALID
    }

}
