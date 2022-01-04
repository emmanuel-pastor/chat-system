package com.simplesmartapps.chatsystem.presentation.network_listing;

import com.simplesmartapps.chatsystem.ChatSystemApplication;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static com.simplesmartapps.chatsystem.presentation.util.ViewState.*;

public class NetworkListingPage implements Initializable {
    private final NetworkListingViewModel mViewModel;

    public NetworkListingPage() {
        mViewModel = ChatSystemApplication.injector.getInstance(NetworkListingViewModel.class);
    }

    @FXML
    private ListView<BroadcastNetwork> networksListView = new ListView<>(FXCollections.emptyObservableList());

    @FXML
    private Button refreshButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    private HBox errorContainer;

    @FXML
    private Text errorTextView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mViewModel.mSate.observe(this, newState -> {
            if (newState == LOADING) {
                networksListView.setVisible(false);
                errorContainer.setVisible(false);
                loadingIndicator.setVisible(true);
            } else if (newState == READY) {
                loadingIndicator.setVisible(false);
                errorContainer.setVisible(false);
                networksListView.setVisible(true);
            } else if (newState == ERROR) {
                loadingIndicator.setVisible(false);
                networksListView.setVisible(false);
                errorContainer.setVisible(true);
            }
        });

        mViewModel.mErrorText.observe(this, newText -> errorTextView.setText(newText));

        mViewModel.mAvailableNetworksList.observe(this, updatedList ->
                networksListView.setItems(FXCollections.observableList(updatedList))
        );
        networksListView.setPlaceholder(emptyListPlaceholder());
        networksListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        networksListView.getSelectionModel().getSelectedIndices().addListener((ListChangeListener<Integer>) change -> {
            if (change.next() && change.wasAdded()) {
                int index = change.getAddedSubList().get(0);
                mViewModel.onListItemClicked(index);
            }
        });
        networksListView.setCellFactory(listView -> new NetworksListCell());

        refreshButton.setOnMouseClicked(event -> mViewModel.onRefreshButtonClicked());
    }

    private VBox emptyListPlaceholder() {
        URL imagePath = this.getClass().getResource("images/no_connection.png");
        assert imagePath != null;
        Image image = new Image(imagePath.toString());
        ImageView imageView = new ImageView(image);

        Text text = new Text("No available network...");
        text.setFont(new Font("System", 20));
        text.setFill(Color.web("#e85125"));

        VBox vbox = new VBox(imageView, text);
        vbox.setSpacing(40);
        vbox.setAlignment(Pos.TOP_CENTER);
        return vbox;
    }
}