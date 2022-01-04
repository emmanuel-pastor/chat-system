package com.simplesmartapps.chatsystem;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.simplesmartapps.chatsystem.data.remote.UDPServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatSystemApplication extends Application {
    public static Injector injector;
    public static Stage appStage;
    public static String username = "Romain";

    @Override
    public void start(Stage stage) throws IOException {
        injector = Guice.createInjector(new CoreModule());
        appStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("presentation/network_listing/network_listing_page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 860, 550);
        stage.setTitle("Chat System");
        stage.setScene(scene);
        stage.show();
        UDPServer udpServer = injector.getInstance(UDPServer.class);
        udpServer.run();
    }

    public static void main(String[] args) {
        launch();
    }
}
