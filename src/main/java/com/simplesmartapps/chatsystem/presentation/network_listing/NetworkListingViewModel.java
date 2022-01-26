package com.simplesmartapps.chatsystem.presentation.network_listing;

import com.google.inject.Inject;
import com.simplesmartapps.chatsystem.data.remote.exception.NetworkListingException;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import com.simplesmartapps.chatsystem.domain.ListAvailableNetworksUseCase;
import com.simplesmartapps.chatsystem.domain.SelectNetworkUseCase;
import com.simplesmartapps.chatsystem.presentation.username_selection.UsernameSelectionPage;
import com.simplesmartapps.chatsystem.presentation.util.NavigationUtil;
import com.simplesmartapps.chatsystem.presentation.util.ObservableProperty;
import com.simplesmartapps.chatsystem.presentation.util.ViewState;
import javafx.concurrent.Task;

import java.util.Collections;
import java.util.List;

public class NetworkListingViewModel {
    private final ListAvailableNetworksUseCase mListAvailableNetworksUseCase;
    private final SelectNetworkUseCase mSelectNetworkUseCase;

    public ObservableProperty<List<BroadcastNetwork>> mAvailableNetworksList = new ObservableProperty<>(Collections.emptyList());
    public ObservableProperty<ViewState> mSate = new ObservableProperty<>(ViewState.READY);
    public ObservableProperty<String> mErrorText = new ObservableProperty<>("");

    @Inject
    public NetworkListingViewModel(ListAvailableNetworksUseCase listAvailableNetworksUseCase, SelectNetworkUseCase selectNetworkUseCase) {
        this.mListAvailableNetworksUseCase = listAvailableNetworksUseCase;
        this.mSelectNetworkUseCase = selectNetworkUseCase;
        refreshAvailableNetworksList();
    }

    public void onListItemClicked(int indexInList) {
        BroadcastNetwork selectedNetwork = mAvailableNetworksList.getValue().get(indexInList);
        mSelectNetworkUseCase.execute(selectedNetwork);

        cleanupObservers();
        NavigationUtil.navigateTo(UsernameSelectionPage.class);
    }

    private void cleanupObservers() {
        mSate.clearObservers();
        mAvailableNetworksList.clearObservers();
        mErrorText.clearObservers();
    }

    public void onRefreshButtonClicked() {
        refreshAvailableNetworksList();
    }

    private void refreshAvailableNetworksList() {
        mSate.setValue(ViewState.LOADING);

        Task<List<BroadcastNetwork>> task = new Task<>() {
            @Override
            protected List<BroadcastNetwork> call() throws Exception {
                return mListAvailableNetworksUseCase.execute();
            }
        };

        task.setOnSucceeded(event -> {
            @SuppressWarnings("unchecked")
            List<BroadcastNetwork> networksList = (List<BroadcastNetwork>) event.getSource().getValue();
            mAvailableNetworksList.setValue(networksList);
            mSate.setValue(ViewState.READY);
        });

        task.setOnFailed(event -> {
            Throwable exception = event.getSource().getException();
            if (NetworkListingException.class.equals(exception.getClass())) {
                mErrorText.setValue("Could not load the available networks list");
            } else {
                exception.printStackTrace();
                mErrorText.setValue("An unexpected error occurred");
            }
            mSate.setValue(ViewState.ERROR);
        });

        new Thread(task).start();
    }
}
