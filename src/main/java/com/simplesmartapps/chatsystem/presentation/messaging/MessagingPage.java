package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
        usersListView.setPlaceholder(emptyListPlaceholder());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        usersListView.setCellFactory(listView -> new UsersListCell());
    }

    private void updateListView(ObservableSet<? extends User> usersSet) {
        usersListView.setItems(FXCollections.observableList(new ArrayList<>(usersSet)));
    }

    private VBox emptyListPlaceholder() {
        URL imagePath = this.getClass().getResource("images/alone.png");
        assert imagePath != null;
        Image image = new Image(imagePath.toString());
        ImageView imageView = new ImageView(image);

        Text text = new Text("You are the only one connected right now");
        text.setFont(new Font("System", 19));

        VBox vbox = new VBox(imageView, text);
        vbox.setSpacing(40);
        vbox.setPadding(new Insets(0, 20, 0, 20));
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }
}
