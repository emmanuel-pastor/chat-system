package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.local.model.Message;
import com.simplesmartapps.chatsystem.data.local.model.User;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.collections.*;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MessagingPage implements Initializable {
    private final MessagingViewModel mViewModel;

    @FXML
    private ListView<UserWithLatestMessage> usersListView;

    @FXML
    private VBox messagingSideContainer;

    @FXML
    private Circle selectedUserStatusCircle;

    @FXML
    private Text selectedUserInitialTextView;

    @FXML
    private Text selectedUserUsernameTextView;

    @FXML
    private ListView<Message> messagesListView;

    @FXML
    private TextField messageTextField;

    @FXML
    private Button sendMessageButton;

    @FXML
    private ProgressIndicator sendMessageLoadingIndicator;

    @FXML
    private Text errorTextView;

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
                if (messageTextField.isVisible()) {
                    // To avoid the focus going back to the users list view thus provoking some weird visual effect
                    selectedUserUsernameTextView.requestFocus();
                }
            } else if (newState == ViewState.READY) {
                sendMessageLoadingIndicator.setVisible(false);
                errorTextView.setVisible(false);
                sendMessageButton.setVisible(true);
                messageTextField.setDisable(false);
                messageTextField.setText("");
                if (messageTextField.isVisible()) {
                    messageTextField.requestFocus();
                }
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

        mViewModel.mUsersSet.addListener((SetChangeListener<? super User>) change -> updateUsersListView(change.getSet(), mViewModel.mLatestMessages));
        mViewModel.mLatestMessages.addListener((ListChangeListener<? super Message>) change -> updateUsersListView(mViewModel.mUsersSet, change.getList()));

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

        mViewModel.mMessages.observe(this, newList -> {
            messagesListView.setItems(newList);
            if (newList != null) {
                messagesListView.scrollTo(newList.size());
                newList.addListener((ListChangeListener<Message>) change -> {
                    messagesListView.scrollTo(change.getList().size());
                });
            }
        });

        sendMessageButton.setOnMouseClicked(event -> {
            mViewModel.onSendButtonClicked(messageTextField.getText());
        });
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
        usersListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<UserWithLatestMessage>) change -> {
            if (change.next() && change.wasAdded()) {
                messagingSideContainer.setVisible(true);
                mViewModel.onListItemClicked(change.getAddedSubList().get(0).user());
            }
        });
        updateUsersListView(mViewModel.mUsersSet, mViewModel.mLatestMessages);
    }

    private void updateUsersListView(ObservableSet<? extends User> usersSet, ObservableList<? extends Message> latestMessages) {
        List<UserWithLatestMessage> userWithLatestMessages = usersSet.stream()
                .map(user -> new UserWithLatestMessage(
                        user,
                        latestMessages.stream()
                                .filter(message -> message.remoteUserId().equals(user.macAddress())).findFirst().orElse(null),
                        true)
                ).collect(Collectors.toList());
        usersListView.setItems(FXCollections.observableList(new ArrayList<>(userWithLatestMessages)));
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
        messagesListView.setFocusTraversable(false);
        messagesListView.setCellFactory(listView -> new MessagesListCell());
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
