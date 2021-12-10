package com.simplesmartapps.chatsystem.presentation.username_selection;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.SelectUsernameException;
import com.simplesmartapps.chatsystem.domain.SelectUsernameUseCase;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.application.Platform;

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
        mState.setValue(LOADING);
        Platform.runLater(() -> {
            try {
                boolean isValid = mSelectUsernameUseCase.execute(username);
                mState.setValue(READY);
                if (isValid) {
                    mIsValid.setValue(VALID);
                } else {
                    mIsValid.setValue(INVALID);
                }
            } catch (SelectUsernameException e) {
                mErrorText.setValue("Could not check username validity");
                mState.setValue(ERROR);
                e.printStackTrace();
            } catch (Exception e) {
                mErrorText.setValue("An unexpected error occurred");
                mState.setValue(ERROR);
                e.printStackTrace();
            }
        });
    }

    enum ValidityState {
        UNKNOWN,
        VALID,
        INVALID
    }

}
