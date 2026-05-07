package org.example.daibetes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.daibetes.shared.utils.NavigationUtils;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = NavigationUtils.load(
                "register",
                "register-screen.fxml",
                "/styles/splash.css"
        );

        if (scene == null) {
            throw new RuntimeException("Failed to load initial screen.");
        }

        stage.setTitle("dAIbetes");
        stage.setScene(scene);
        stage.show();
    }
}