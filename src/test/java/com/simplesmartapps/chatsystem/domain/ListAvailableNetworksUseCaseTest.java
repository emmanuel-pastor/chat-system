package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.simplesmartapps.chatsystem.CoreTestModule;
import com.simplesmartapps.chatsystem.data.remote.NetworkListingException;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.util.List;

public class ListAvailableNetworksUseCaseTest {
    ListAvailableNetworksUseCase mUseCase;

    @BeforeEach
    void setUp() {
        Injector injector = Guice.createInjector(new CoreTestModule());
        mUseCase = injector.getInstance(ListAvailableNetworksUseCase.class);
    }

    @Test
    void shouldGetListOfNetworks() throws NetworkListingException {
        List<Pair<InetAddress, String>> list = mUseCase.execute();

        assert !list.isEmpty();
        assert list.get(0).getKey().getHostAddress().equals("127.0.0.1");
        assert list.get(0).getValue().equals("Loopback network interface");
    }
}
