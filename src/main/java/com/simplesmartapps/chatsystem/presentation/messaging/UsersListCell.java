package com.simplesmartapps.chatsystem.presentation.messaging;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

public class UsersListCell extends ListCell<UserWithLatestMessage> {
    @FXML
    private GridPane gridPane;

    @FXML
    private Circle statusCircle;

    @FXML
    private Text usernameInitialTextView;

    @FXML
    private Text usernameTextView;

    @FXML
    private Text lastMessageTextView;

    public UsersListCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader mLoader = new FXMLLoader(getClass().getResource("users_list-cell.fxml"));
        mLoader.setController(this);
        try {
            mLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(UserWithLatestMessage userWithLatestMessage, boolean empty) {
        super.updateItem(userWithLatestMessage, empty);

        if (empty || userWithLatestMessage == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (userWithLatestMessage.user().isConnected()) {
                statusCircle.setFill(Color.web("#38b22b"));
            } else {
                statusCircle.setFill(Color.DARKGRAY);
            }
            usernameInitialTextView.setText(userWithLatestMessage.user().username().substring(0, 1).toUpperCase());
            usernameTextView.setText(userWithLatestMessage.user().username());
            if (userWithLatestMessage.latestMessage() != null) {
                if (userWithLatestMessage.latestMessage().isIncoming()) {
                    lastMessageTextView.setText(userWithLatestMessage.user().username() + ": " + userWithLatestMessage.latestMessage().content());
                } else {
                    lastMessageTextView.setText("You: " + userWithLatestMessage.latestMessage().content());
                }
            } else {
                lastMessageTextView.setText("");
            }

            setText(null);
            setGraphic(gridPane);
        }
    }
}
