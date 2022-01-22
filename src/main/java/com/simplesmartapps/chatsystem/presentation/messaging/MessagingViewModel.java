package com.simplesmartapps.chatsystem.presentation.messaging;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.domain.ListUsersUseCase;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.ObservableSet;

public class MessagingViewModel {
    private final RuntimeDataStore mRunTimeDataStore;
    public ObservableSet<User> mUsersSet;
    public ObservableProperty<User> mSelectedUser = new ObservableProperty<>(null);
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public MessagingViewModel(RuntimeDataStore mRunTimeDataStore, ListUsersUseCase listUsersUseCase) {
        this.mRunTimeDataStore = mRunTimeDataStore;
        mUsersSet = listUsersUseCase.execute();
    }

    public void onListItemClicked(int index) {
        User selectedUser = (User) mRunTimeDataStore.readUsersSet().toArray()[index];
        mSelectedUser.setValue(selectedUser);
    }
}

