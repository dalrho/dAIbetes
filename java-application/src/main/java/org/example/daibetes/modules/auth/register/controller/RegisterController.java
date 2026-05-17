package org.example.daibetes.modules.auth.register.controller;

import org.example.daibetes.shared.ui.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class RegisterController {

    @FXML
    public void selectPatient(ActionEvent event) {
        // Logic remains the same, helper method updated to handle full screen
        openScene(event, "org/example/daibetes/modules/auth/register", "patient-register.fxml", "/org/example/daibetes/styles/splash2.css");
    }

    @FXML
    public void selectDoctor(ActionEvent event) {
        // Logic remains the same, helper method updated to handle full screen
        openScene(event, "org/example/daibetes/modules/auth/register", "doctor-register.fxml", "/org/example/daibetes/styles/splash2.css");
    }

    @FXML
    public void goToLogin(MouseEvent event) {
        // FIX: Replaced manual setScene with switchScene to maintain Kiosk/Full Screen mode
        SceneLoader.switchScene(
                (Node) event.getSource(),
                "org/example/daibetes/modules/auth/login",
                "login-screen.fxml",
                "Login - dAIbetes",
                "/org/example/daibetes/styles/splash2.css"
        );
    }

    private void openScene(ActionEvent event, String folder, String fxml, String css) {
        // FIX: Replaced manual try-catch boilerplate with the centralized switchScene utility.
        // This ensures the "Kiosk" full-screen mode is re-applied during the transition.
        SceneLoader.switchScene(
                (Node) event.getSource(),
                folder,
                fxml,
                "Register - dAIbetes",
                css
        );
    }
}