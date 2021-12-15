package com.simplesmartapps.chatsystem.presentation.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.simplesmartapps.chatsystem.ChatSystemApplication.appStage;

public class NavigationUtil {

    public static <T> void navigateTo(Class<T> pageClass) {
        navigate(pageClass, null);
    }

    public static <T> void navigateTo(Class<T> pageClass, Map<String, Object> args) {
        navigate(pageClass, args);
    }

    private static <T> void navigate(Class<T> pageClass, Map<String, Object> args) {
        try {
            appStage.setUserData(args);

            String className = pageClass.getSimpleName();
            String classNameSnakeCase = camelToSnakeCase(className);
            Parent root = FXMLLoader.load(Objects.requireNonNull(pageClass.getResource(classNameSnakeCase + "-view.fxml")));

            Scene scene = new Scene(root);

            appStage.setScene(scene);
            appStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String camelToSnakeCase(String camelStr) {
        String ret = camelStr.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z])([A-Z])", "$1_$2");
        return ret.toLowerCase();
    }
}
