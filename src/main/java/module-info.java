module com.simplesmartapps.chatsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.json;
    requires java.desktop;

    opens com.simplesmartapps.chatsystem to javafx.fxml;
    exports com.simplesmartapps.chatsystem;
}