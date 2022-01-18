package com.simplesmartapps.chatsystem;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatSystemApplication extends Application {
    public static Injector injector;
    public static Stage appStage;

    @Override
    public void start(Stage stage) throws IOException {
        injector = Guice.createInjector(new CoreDIModule());
        appStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("presentation/network_listing/network_listing_page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 860, 550);
        stage.setTitle("Chat System");
        stage.setScene(scene);
        stage.show();
        TCPServer tcpServer = new TCPServer();
        tcpServer.start(6666);
        TCPClient tcpClient1 = new TCPClient();
        TCPClient tcpClient2 = new TCPClient();
        tcpClient1.startConnection("127.0.0.1", 6666);
//        String msg11 = tcpClient1.sendMessage("hello");
//        String msg21 = tcpClient2.sendMessage("hello");
//        String msg12 = tcpClient1.sendMessage("world");
//        String msg22 = tcpClient2.sendMessage("world");
//        String terminate1 = tcpClient1.sendMessage(".");
//        String terminate2 = tcpClient2.sendMessage(".");
    }

    public static void main(String[] args) {
        launch();
    }
}
