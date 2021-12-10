module com.simplesmartapps.chatsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires com.google.guice;
    requires org.json;
    requires java.desktop;

    opens com.simplesmartapps.chatsystem to javafx.fxml;
    exports com.simplesmartapps.chatsystem;
    exports com.simplesmartapps.chatsystem.data.remote;
    exports com.simplesmartapps.chatsystem.domain;
    exports com.simplesmartapps.chatsystem.presentation.util;
    exports com.simplesmartapps.chatsystem.presentation.network_listing;
    opens com.simplesmartapps.chatsystem.presentation.username_selection;
    opens com.simplesmartapps.chatsystem.presentation.network_listing to javafx.fxml;
}