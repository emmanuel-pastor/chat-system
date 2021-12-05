package com.simplesmartapps.chatsystem.presentation.network_listing;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.IOException;
import java.net.InetAddress;

public class NetworksListCell extends ListCell<Pair<InetAddress, String>> {
    @FXML
    private GridPane gridPane;

    @FXML
    private Text interfaceName;

    @FXML
    private Text broadcastAddress;

    public NetworksListCell() {
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader mLoader = new FXMLLoader(getClass().getResource("networks_list-cell.fxml"));
        mLoader.setController(this);
        try {
            mLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateItem(Pair<InetAddress, String> pair, boolean empty) {
        super.updateItem(pair, empty);

        if (empty || pair == null) {
            setText(null);
            setGraphic(null);
        } else {
            interfaceName.setText(pair.getValue());
            broadcastAddress.setText(pair.getKey().getHostName());

            setText(null);
            setGraphic(gridPane);
        }
    }
}
