package com.simplesmartapps.chatsystem.domain;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.simplesmartapps.chatsystem.CoreTestModule;
import com.simplesmartapps.chatsystem.data.remote.exception.NetworkListingException;
import com.simplesmartapps.chatsystem.data.remote.model.BroadcastNetwork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        List<BroadcastNetwork> list = mUseCase.execute();

        assert !list.isEmpty();
        assert list.get(0).address().getHostAddress().equals("127.0.0.1");
        assert list.get(0).interfaceName().equals("Loopback network interface");
    }
}
