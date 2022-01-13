package com.simplesmartapps.chatsystem.presentation.messaging_page;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.domain.ListUsersUseCase;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.ObservableSet;

public class MessagingViewModel {
    public ObservableSet<User> mUsersSet;
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public MessagingViewModel(ListUsersUseCase listUsersUseCase) {
        mUsersSet = listUsersUseCase.execute();
    }
}
