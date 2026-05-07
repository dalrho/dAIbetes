package org.example.daibetes.modules.auth.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.example.daibetes.shared.utils.NavigationUtils;

public class RegisterController {

    @FXML
    private void selectPatient(ActionEvent event) {
        NavigationUtils.switchScene(
                (Node) event.getSource(),
                "register",
                "patient-register.fxml",
                "dAIbetes — Patient Registration",
                null
        );
    }

    @FXML
    private void selectDoctor(ActionEvent event) {
        NavigationUtils.switchScene(
                (Node) event.getSource(),
                "register",
                "doctor-register.fxml",
                "dAIbetes — Doctor Registration",
                null
        );
    }

    @FXML
    private void goToLogin(MouseEvent event) {
        NavigationUtils.switchScene(
                (Node) event.getSource(),
                "login",
                "login-screen.fxml",
                "dAIbetes — Login",
                null
        );
    }
}