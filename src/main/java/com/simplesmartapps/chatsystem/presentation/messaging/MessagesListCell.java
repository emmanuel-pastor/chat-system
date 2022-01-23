package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.data.local.model.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class MessagesListCell extends ListCell<Message> {
    @FXML
    private HBox mainContainer;

    @FXML
    private GridPane cellContainer;

    @FXML
    private HBox messageContentContainer;

    @FXML
    private Text messageContentTextView;

    @FXML
    private Text dateTextView;

    public MessagesListCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader mLoader = new FXMLLoader(getClass().getResource("messages_list-cell.fxml"));
        mLoader.setController(this);
        try {
            mLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (message.isIncoming()) {
                cellContainer.setAlignment(Pos.CENTER_LEFT);
                messageContentTextView.setFill(Color.BLACK);
                messageContentContainer.setBackground(new Background(new BackgroundFill(Color.web("#e4e6eb;"), null, null)));
            } else {
                cellContainer.setAlignment(Pos.CENTER_RIGHT);
                messageContentTextView.setFill(Color.WHITE);
                messageContentContainer.setBackground(new Background(new BackgroundFill(Color.web("#3984ff;"), null, null)));
            }
            messageContentTextView.setText(message.content());
            dateTextView.setText(formatDate(message.timestamp()));

            setText(null);
            setGraphic(mainContainer);
        }
    }

    private String formatDate(long timestamp) {
        String pattern = "MMMM Do, LT";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = Date.from(Instant.ofEpochMilli(timestamp));

        return simpleDateFormat.format(date);
    }
}
