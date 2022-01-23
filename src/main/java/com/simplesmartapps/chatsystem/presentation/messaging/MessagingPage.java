package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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

    @FXML
    public ProgressIndicator sendMessageLoadingIndicator;

    @FXML
    public Text errorTextView;

    public MessagingPage() {
        this.mViewModel = ChatSystemApplication.injector.getInstance(MessagingViewModel.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mViewModel.mSate.observe(this, newState -> {
            if (newState == ViewState.LOADING) {
                messageTextField.setDisable(true);
                errorTextView.setVisible(false);
                sendMessageButton.setVisible(false);
                sendMessageLoadingIndicator.setVisible(true);
            } else if (newState == ViewState.READY) {
                sendMessageLoadingIndicator.setVisible(false);
                errorTextView.setVisible(false);
                sendMessageButton.setVisible(true);
                messageTextField.setDisable(false);
                messageTextField.setText("");
            } else if (newState == ViewState.ERROR) {
                sendMessageLoadingIndicator.setVisible(false);
                messageTextField.setDisable(false);
                sendMessageButton.setVisible(true);
                errorTextView.setVisible(true);
            }
        });

        mViewModel.mErrorText.observe(this, newErrorText -> errorTextView.setText(newErrorText));

        setUpUsersListView();
        setUpMessagesListView();

        mViewModel.mUsersSet.addListener((SetChangeListener<? super User>) change -> updateUsersListView(change.getSet()));

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

        sendMessageButton.setOnMouseClicked(event -> mViewModel.onSendButtonClicked(messageTextField.getText()));
        messageTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mViewModel.onEnterKeyPressed(messageTextField.getText());
            }
        });
    }

    private void setUpUsersListView() {
        usersListView.setPlaceholder(emptyUsersListPlaceholder());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        usersListView.setCellFactory(listView -> new UsersListCell());
        usersListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<User>) change -> {
            if (change.next() && change.wasAdded()) {
                messagingSideContainer.setVisible(true);
                mViewModel.onListItemClicked(change.getAddedSubList().get(0));
            }
        });
        updateUsersListView(mViewModel.mUsersSet);
    }

    private void updateUsersListView(ObservableSet<? extends User> usersSet) {
        usersListView.setItems(FXCollections.observableList(new ArrayList<>(usersSet)));
    }

    private VBox emptyUsersListPlaceholder() {
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

    private void setUpMessagesListView() {
        messagesListView.setPlaceholder(emptyMessagesListPlaceholder());
        messagesListView.setMouseTransparent(true);
        messagesListView.setFocusTraversable(false);
    }

    private VBox emptyMessagesListPlaceholder() {
        URL imagePath = this.getClass().getResource("images/no_message.png");
        assert imagePath != null;
        Image image = new Image(imagePath.toString());
        ImageView imageView = new ImageView(image);

        Text textView = new Text("No message found");

        VBox vbox = new VBox(imageView, textView);
        vbox.setSpacing(40);
        vbox.setPadding(new Insets(0, 20, 0, 20));
        vbox.setAlignment(Pos.CENTER);

        return vbox;
    }
}
