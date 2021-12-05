package com.simplesmartapps.chatsystem.presentation.network_listing;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.NetworkListingException;
import com.simplesmartapps.chatsystem.domain.ListAvailableNetworksUseCase;
import com.simplesmartapps.chatsystem.domain.SelectNetworkUseCase;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.application.Platform;
import javafx.util.Pair;

import java.net.InetAddress;
import java.util.Collections;
import java.util.List;

public class NetworkListingViewModel {
    private final ListAvailableNetworksUseCase mListAvailableNetworksUseCase;
    private final SelectNetworkUseCase mSelectNetworkUseCase;

    public ObservableProperty<List<Pair<InetAddress, String>>> mAvailableNetworksList = new ObservableProperty<>(Collections.emptyList());
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public NetworkListingViewModel(ListAvailableNetworksUseCase listAvailableNetworksUseCase, SelectNetworkUseCase selectNetworkUseCase) {
        this.mListAvailableNetworksUseCase = listAvailableNetworksUseCase;
        this.mSelectNetworkUseCase = selectNetworkUseCase;
        refreshAvailableNetworksList();
    }

    public void onListItemClicked(int indexInList) {
        InetAddress selectedNetwork = mAvailableNetworksList.getValue().get(indexInList).getKey();
        mSelectNetworkUseCase.execute(selectedNetwork);
    }

    public void onRefreshButtonClicked() {
        refreshAvailableNetworksList();
    }

    private void refreshAvailableNetworksList() {
        mSate.setValue(ViewState.LOADING);
        Platform.runLater(() -> {
            try {
                mAvailableNetworksList.setValue(mListAvailableNetworksUseCase.execute());
                mSate.setValue(ViewState.READY);
            } catch (NetworkListingException e) {
                mErrorText.setValue("Could not load the available networks list");
                mSate.setValue(ViewState.ERROR);
                e.printStackTrace();
            } catch (Exception e) {
                mErrorText.setValue("An unexpected error occurred");
                mSate.setValue(ViewState.ERROR);
                e.printStackTrace();
            }
        });
    }
}
