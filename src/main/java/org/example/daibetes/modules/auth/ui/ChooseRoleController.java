package org.example.daibetes.modules.auth.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ChooseRoleController {
    @FXML private Button patient;
    @FXML private Button doctor;

    public void goToPatientRegister() {
        loadPage("patientRegister.fxml", patient);
    }

    public void goToDoctorRegister() {
        loadPage("doctorRegister.fxml", doctor);
    }

    public void loadPage(String fxml, Button button) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/register/" + fxml)
            );

            Scene scene = new Scene(loader.load());

            // get current stage from button
            Stage stage = (Stage) button.getScene().getWindow();

            stage.setTitle("Diabetes Detection System");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
