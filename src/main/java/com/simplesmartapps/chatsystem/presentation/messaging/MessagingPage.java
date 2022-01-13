package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MessagingPage implements Initializable {
    private final MessagingViewModel mViewModel;

    @FXML
    public ListView<User> usersListView;

    public MessagingPage() {
        this.mViewModel = ChatSystemApplication.injector.getInstance(MessagingViewModel.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateListView(mViewModel.mUsersSet);
        mViewModel.mUsersSet.addListener((SetChangeListener<? super User>) change -> {
            updateListView(change.getSet());
        });
    }

    private void updateListView(ObservableSet<? extends User> usersSet) {
        usersListView.setItems(FXCollections.observableList(new ArrayList<>(usersSet)));
    }
}
