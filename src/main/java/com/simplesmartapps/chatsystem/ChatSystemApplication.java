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
        tcpServer.start(5555);
        TCPClient tcpClient1 = new TCPClient();
        tcpClient1.startConnection("127.0.0.1", 5555);
        String msg11 = tcpClient1.sendMessage("hello");
        String msg12 = tcpClient1.sendMessage("world");
        String msg1end = tcpClient1.sendMessage(".");
        System.out.println("message fin " + msg1end);

        TCPClient tcpClient2 = new TCPClient();
        tcpClient2.startConnection("127.0.0.1", 5555);
        String msg21 = tcpClient2.sendMessage("hello2");
        String msg22 = tcpClient2.sendMessage("world2");
        String msg2end = tcpClient2.sendMessage(".");
        System.out.println("message fin " + msg2end);
    }

    public static void main(String[] args) {
        launch();
    }
}
