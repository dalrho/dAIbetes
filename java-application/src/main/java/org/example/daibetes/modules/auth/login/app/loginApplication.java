package org.example.daibetes.modules.auth.login.app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

public class loginApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(
                SceneLoader.load(
                        "org/example/daibetes/modules/auth/login",
                        "login-screen.fxml",
                        "/org/example/daibetes/styles/splash.css"
                )
        );
        stage.setTitle("dAIbetes - Login");
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}