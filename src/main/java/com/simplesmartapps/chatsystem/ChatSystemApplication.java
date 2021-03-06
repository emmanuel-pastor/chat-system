package com.simplesmartapps.chatsystem;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ChatSystemApplication extends Application {
    public static Injector injector;
    public static Stage appStage;

    @Override
    public void start(Stage stage) throws IOException {
        injector = Guice.createInjector(new CoreDIModule());
        appStage = stage;
        URL imagePath = this.getClass().getResource("images/icon.png");
        assert imagePath != null;
        stage.getIcons().add(new Image(imagePath.toString()));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("presentation/network_listing/network_listing_page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 860, 550);
        stage.setTitle("Chat System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
