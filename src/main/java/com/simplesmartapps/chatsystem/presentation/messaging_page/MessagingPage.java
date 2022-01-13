package com.simplesmartapps.chatsystem.presentation.messaging_page;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.FXCollections;
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
        mViewModel.mUsersSet.addListener((SetChangeListener<? super User>) change -> {
            usersListView.setItems(FXCollections.observableList(new ArrayList<>(change.getSet())));
        });
    }
}
