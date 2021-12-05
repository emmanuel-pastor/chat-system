package com.simplesmartapps.chatsystem;

import com.google.inject.AbstractModule;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.NetworkControllerFake;

public class CoreTestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NetworkController.class).toInstance(NetworkControllerFake.getInstance());
    }
}
