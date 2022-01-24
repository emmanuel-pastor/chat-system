package com.simplesmartapps.chatsystem.presentation.messaging;

import com.simplesmartapps.chatsystem.data.local.model.Message;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
    private Label messageContentLabel;

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
                messageContentLabel.setTextFill(Color.BLACK);
                messageContentContainer.setStyle("-fx-background-radius: 100; -fx-background-color: #e4e6eb;");
            } else {
                cellContainer.setAlignment(Pos.CENTER_RIGHT);
                messageContentLabel.setTextFill(Color.WHITE);
                messageContentContainer.setStyle("-fx-background-radius: 100; -fx-background-color: #3984ff;");
            }
            messageContentLabel.setText(message.content());
            dateTextView.setText(formatDate(message.timestamp()));

            setText(null);
            setGraphic(mainContainer);
        }
    }

    private String formatDate(long timestamp) {
        Date messageDate = Date.from(Instant.ofEpochMilli(timestamp));
        boolean wasSentToday = isSameDay(messageDate, new Date());
        String pattern;
        if (wasSentToday) {
            pattern = "HH:mm";
        } else {
            pattern = "d MMMM, HH:mm";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(messageDate);
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
