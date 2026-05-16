package org.example.daibetes.modules.splash.controller;


import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.daibetes.shared.ui.SceneLoader;

public class SplashController {

    @FXML
    private VBox rootContainer; // Make sure this matches fx:id in FXML

    // Runs automatically when FXML loads
    @FXML
    public void initialize() {
        playFadeIn();
    }

    private void playFadeIn() {
        rootContainer.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(1200), rootContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.play();
    }

    // START BUTTON → GO TO LOGIN
    @FXML
    public void handleStart(ActionEvent event) {
        // We use the central utility which handles the Full Screen logic for us
        SceneLoader.switchScene(
                (Node) event.getSource(),                         // The button clicked
                "org/example/daibetes/modules/auth/login",        // Folder path
                "login-screen.fxml",                              // FXML filename
                "Login - dAIbetes",                               // Window Title
                "/org/example/daibetes/styles/splash.css"         // CSS path
        );
    }
}
