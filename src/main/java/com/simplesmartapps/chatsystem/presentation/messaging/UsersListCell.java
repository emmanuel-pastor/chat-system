package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.data.local.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.io.IOException;

public class UsersListCell extends ListCell<User> {
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
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (user.isConnected()) {
                statusCircle.setFill(Color.web("#38b22b"));
            } else {
                statusCircle.setFill(Color.DARKGRAY);
            }
            usernameInitialTextView.setText(user.username().substring(0, 1).toUpperCase());
            usernameTextView.setText(user.username());

            setText(null);
            setGraphic(gridPane);
        }
    }
}
