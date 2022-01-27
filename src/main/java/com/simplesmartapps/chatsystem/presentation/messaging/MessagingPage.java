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
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

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
    private Label selectedUserUsernameLabel;

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

    @FXML
    private Button editUsernameButton;

    @FXML
    private Button validateUsernameButton;

    @FXML
    private ProgressIndicator usernameValidationLoadingIndicator;

    @FXML
    private FontIcon editUsernameErrorIcon;

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Label editUsernameErrorLabel;

    @FXML
    private Button disconnectionButton;

    public MessagingPage() {
        this.mViewModel = ChatSystemApplication.injector.getInstance(MessagingViewModel.class);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mViewModel.mMessagingSate.observe(this, newState -> {
            if (newState == ViewState.LOADING) {
                messageTextField.setDisable(true);
                errorTextView.setVisible(false);
                sendMessageButton.setVisible(false);
                sendMessageLoadingIndicator.setVisible(true);
                if (messageTextField.isVisible()) {
                    // To avoid the focus going back to the users list view thus provoking some weird visual effect
                    selectedUserUsernameLabel.requestFocus();
                }
            } else if (newState == ViewState.READY) {
                sendMessageLoadingIndicator.setVisible(false);
                errorTextView.setVisible(false);
                sendMessageButton.setVisible(true);
                if (mViewModel.mSelectedUser.getValue() != null && mViewModel.mSelectedUser.getValue().isConnected()) {
                    messageTextField.setDisable(false);
                    messageTextField.setText("");
                    if (messageTextField.isVisible()) {
                        messageTextField.requestFocus();
                    }
                }
            } else if (newState == ViewState.ERROR) {
                sendMessageLoadingIndicator.setVisible(false);
                messageTextField.setDisable(false);
                sendMessageButton.setVisible(true);
                errorTextView.setVisible(true);
            }
        });

        mViewModel.mMessagingErrorText.observe(this, newErrorText -> errorTextView.setText(newErrorText));

        setUpUsersListView();
        setUpMessagesListView();

        mViewModel.mKnownUsers.addListener(knownUsersListener());
        mViewModel.mLatestMessages.addListener(latestMessagesListener());

        mViewModel.mSelectedUser.observe(this, selectedUser -> {
            if (selectedUser != null) {
                selectedUserUsernameLabel.setText(selectedUser.username());
                selectedUserInitialTextView.setText(selectedUser.username().substring(0, 1).toUpperCase());
                if (selectedUser.isConnected()) {
                    selectedUserStatusCircle.setFill(Color.web("#38b22b"));
                    messageTextField.setDisable(false);
                    sendMessageButton.setDisable(false);
                } else {
                    selectedUserStatusCircle.setFill(Color.DARKGRAY);
                    messageTextField.setDisable(true);
                    sendMessageButton.setDisable(true);
                }
            }
        });

        mViewModel.mMessages.observe(this, newList -> {
            messagesListView.setItems(newList);
            if (newList != null) {
                messagesListView.scrollTo(newList.size());
                newList.addListener((ListChangeListener<Message>) change -> messagesListView.scrollTo(change.getList().size()));
            }
        });

        mViewModel.mUsername.observe(this, newUsername -> usernameLabel.setText(newUsername));

        sendMessageButton.setOnMouseClicked(event -> mViewModel.onSendButtonClicked(messageTextField.getText()));
        messageTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mViewModel.onMessageTextFieldEnterKeyPressed(messageTextField.getText());
            }
        });


        mViewModel.mUsernameEditionState.observe(this, newState -> {
            if (newState == ViewState.LOADING) {
                usernameTextField.setDisable(true);
                validateUsernameButton.setVisible(false);
                editUsernameErrorIcon.setVisible(false);
                editUsernameErrorLabel.setVisible(false);
                editUsernameButton.setVisible(false);
                usernameValidationLoadingIndicator.setVisible(true);
            } else if (newState == ViewState.READY) {
                usernameTextField.setVisible(false);
                usernameTextField.setDisable(false);
                editUsernameErrorIcon.setVisible(false);
                editUsernameErrorLabel.setVisible(false);
                usernameValidationLoadingIndicator.setVisible(false);
                validateUsernameButton.setVisible(false);
                usernameLabel.setVisible(true);
                editUsernameButton.setVisible(true);
            } else if (newState == ViewState.ERROR) {
                usernameValidationLoadingIndicator.setVisible(false);
                editUsernameButton.setVisible(false);
                usernameTextField.setDisable(false);
                usernameTextField.requestFocus();
                editUsernameErrorIcon.setVisible(true);
                editUsernameErrorLabel.setVisible(true);
                validateUsernameButton.setVisible(true);
            }
        });
        mViewModel.mUsernameEditionErrorText.observe(this, newText -> editUsernameErrorLabel.setText(newText));
        editUsernameButton.setOnMouseClicked(event -> {
            usernameLabel.setVisible(false);
            editUsernameButton.setVisible(false);
            usernameTextField.setVisible(true);
            usernameTextField.setText(mViewModel.mUsername.getValue());
            usernameTextField.requestFocus();
            validateUsernameButton.setVisible(true);
        });
        validateUsernameButton.setOnMouseClicked(event -> mViewModel.onValidateUsernameButtonClicked(usernameTextField.getText()));
        usernameTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mViewModel.onUsernameTextFieldEnterKeyPressed(usernameTextField.getText());
            } else if (event.getCode() == KeyCode.ESCAPE) {
                mViewModel.onUsernameTextFieldEscapeKeyPressed();
            }
        });

        disconnectionButton.setOnMouseClicked(event -> {
            cleanUp();
            mViewModel.onDisconnectionButtonClicked();
        });
    }

    private ListChangeListener<? super Message> latestMessagesListener() {
        return change -> updateUsersListView(mViewModel.mKnownUsers, change.getList());
    }

    private MapChangeListener<? super String, ? super User> knownUsersListener() {
        return change -> {
            updateUsersListView(change.getMap(), mViewModel.mLatestMessages);
        };
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
        updateUsersListView(mViewModel.mKnownUsers, mViewModel.mLatestMessages);
    }

    private void updateUsersListView(ObservableMap<? extends String, ? extends User> knownUsers, ObservableList<? extends Message> latestMessages) {
        ObservableList<UserWithLatestMessage> usersWithLatestMessages = FXCollections.observableArrayList();
        knownUsers.forEach((id, user) -> {
            usersWithLatestMessages.add(
                    new UserWithLatestMessage(
                            user,
                            latestMessages.stream()
                                    .filter(message -> message.remoteUserId().equals(user.macAddress())).findFirst().orElse(null),
                            true
                    )
            );
        });
        usersListView.setItems(FXCollections.observableList(usersWithLatestMessages));
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

    private void cleanUp() {
        mViewModel.mKnownUsers.removeListener(knownUsersListener());
        mViewModel.mLatestMessages.removeListener(latestMessagesListener());
    }
}
