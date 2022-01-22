package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MessagingPage implements Initializable {
    private final MessagingViewModel mViewModel;

    @FXML
    public ListView<User> usersListView;

    @FXML
    public VBox messagingSideContainer;

    @FXML
    public Circle selectedUserStatusCircle;

    @FXML
    public Text selectedUserInitialTextView;

    @FXML
    public Text selectedUserUsernameTextView;

    @FXML
    public ListView<String> messagesListView;

    @FXML
    public TextField messageTextField;

    @FXML
    public Button sendMessageButton;

    public MessagingPage() {
        this.mViewModel = ChatSystemApplication.injector.getInstance(MessagingViewModel.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateListView(mViewModel.mUsersSet);
        mViewModel.mUsersSet.addListener((SetChangeListener<? super User>) change -> updateListView(change.getSet()));
        mViewModel.mSelectedUser.observe(this, selectedUser -> {
            if (selectedUser != null) {
                selectedUserUsernameTextView.setText(selectedUser.username());
                selectedUserInitialTextView.setText(selectedUser.username().substring(0, 1).toUpperCase());
                if (selectedUser.isConnected()) {
                    selectedUserStatusCircle.setFill(Color.web("#38b22b"));
                } else {
                    selectedUserStatusCircle.setFill(Color.DARKGRAY);
                }
            }
        });
        usersListView.setPlaceholder(emptyListPlaceholder());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        usersListView.setCellFactory(listView -> new UsersListCell());
        usersListView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) change -> {
            if (change.next() && change.wasAdded()) {
                messagingSideContainer.setVisible(true);
                int index = change.getAddedSubList().get(0);
                mViewModel.onListItemClicked(index);
            }
        });
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
