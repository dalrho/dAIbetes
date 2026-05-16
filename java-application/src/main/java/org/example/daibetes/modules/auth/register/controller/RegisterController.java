package org.example.daibetes.modules.auth.register.controller;

import org.example.daibetes.shared.ui.SceneLoader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class RegisterController {

    @FXML
    public void selectPatient(ActionEvent event) {
        openScene(event, "org/example/daibetes/modules/auth/register", "patient-register.fxml", "/org/example/daibetes/styles/splash.css");
    }

    @FXML
    public void selectDoctor(ActionEvent event) {
        openScene(event, "org/example/daibetes/modules/auth/register", "doctor-register.fxml", "/org/example/daibetes/styles/splash.css");
    }

    @FXML
    public void goToLogin(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                SceneLoader.load("org/example/daibetes/modules/auth/login", "login-screen.fxml", "/org/example/daibetes/styles/splash.css")
        );
    }

    private void openScene(ActionEvent event, String folder, String fxml, String css) {
        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(SceneLoader.load(folder, fxml, css));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}