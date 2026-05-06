package org.example.daibetes.modules.auth.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ChooseRoleController {

    @FXML private Button patient;
    @FXML private Button doctor;

    @FXML
    public void goToPatientRegister() {
        loadPage("patient-register-screen.fxml", patient);
    }

    @FXML
    public void goToDoctorRegister() {
        loadPage("doctor-register-screen.fxml", doctor);
    }

    private void loadPage(String fxml, Button button) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/register/" + fxml)
            );
            Parent root = loader.load();

            Stage stage = (Stage) button.getScene().getWindow();
            stage.setTitle("dAIbetes — Diabetes Early Detection System");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}