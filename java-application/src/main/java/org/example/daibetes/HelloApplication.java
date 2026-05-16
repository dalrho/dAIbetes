package org.example.daibetes;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/auth/register",
                "register-screen.fxml",
                "/org/example/daibetes/styles/splash.css"
        );

        if (scene == null) {
            throw new RuntimeException("Failed to load initial screen.");
        }

        stage.setTitle("dAIbetes");
        stage.setScene(scene);
        stage.show();
    }
}