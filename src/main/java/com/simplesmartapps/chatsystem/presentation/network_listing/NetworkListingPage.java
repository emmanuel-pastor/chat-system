package com.simplesmartapps.chatsystem.presentation.network_listing;

import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.NetworkControllerImpl;
import com.simplesmartapps.chatsystem.domain.ListAvailableNetworksUseCase;
import com.simplesmartapps.chatsystem.domain.SelectNetworkUseCase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

public class NetworkListingPage implements Initializable {
    private final NetworkController networkController = NetworkControllerImpl.getInstance();
    private final ListAvailableNetworksUseCase listAvailableNetworksUseCase = new ListAvailableNetworksUseCase(networkController);
    private final SelectNetworkUseCase selectNetworkUseCase = new SelectNetworkUseCase(networkController);
    private final NetworkListingViewModel mViewModel = new NetworkListingViewModel(listAvailableNetworksUseCase, selectNetworkUseCase);

    @FXML
    private ListView<Pair<InetAddress, String>> networksListView = new ListView<>(FXCollections.emptyObservableList());

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
            switch (newState) {
                case LOADING -> {
                    networksListView.setVisible(false);
                    errorContainer.setVisible(false);
                    loadingIndicator.setVisible(true);
                }
                case READY -> {
                    loadingIndicator.setVisible(false);
                    errorContainer.setVisible(false);
                    networksListView.setVisible(true);
                }
                case ERROR -> {
                    loadingIndicator.setVisible(false);
                    networksListView.setVisible(false);
                    errorContainer.setVisible(true);
                }
            }
        });

        mViewModel.mErrorText.observe(this, newText -> errorTextView.setText(newText));

        mViewModel.mAvailableNetworksList.observe(this, updatedList ->
                networksListView.setItems(FXCollections.observableList(updatedList))
        );
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
}