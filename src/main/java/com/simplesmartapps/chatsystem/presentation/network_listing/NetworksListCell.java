package com.simplesmartapps.chatsystem.presentation.network_listing;

import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;

public class NetworksListCell extends ListCell<BroadcastNetwork> {
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
    protected void updateItem(BroadcastNetwork network, boolean empty) {
        super.updateItem(network, empty);

        if (empty || network == null) {
            setText(null);
            setGraphic(null);
        } else {
            interfaceName.setText(network.interfaceName());
            broadcastAddress.setText(network.address().getHostName());

            setText(null);
            setGraphic(gridPane);
        }
    }
}
