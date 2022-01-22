package com.simplesmartapps.chatsystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.simplesmartapps.chatsystem.data.local.DatabaseController;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStore;
import com.simplesmartapps.chatsystem.data.local.RuntimeDataStoreImpl;
import com.simplesmartapps.chatsystem.data.local.dao.MessageDao;
import com.simplesmartapps.chatsystem.data.remote.NetworkController;
import com.simplesmartapps.chatsystem.data.remote.NetworkControllerImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CoreDIModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(NetworkController.class).toInstance(NetworkControllerImpl.getInstance());
        bind(RuntimeDataStore.class).toInstance(RuntimeDataStoreImpl.getInstance());
        bind(DatabaseController.class).in(Singleton.class);
        bind(MessageDao.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Connection provideDbConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:chat_system.db");
    }
}
