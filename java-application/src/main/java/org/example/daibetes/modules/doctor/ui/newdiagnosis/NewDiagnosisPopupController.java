package org.example.daibetes.modules.doctor.ui.newdiagnosis;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import register.sceneLoader;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

public class NewDiagnosisPopupController {
    @FXML private ComboBox<String> patientNameBox;


    @FXML
    public void initialize() {
        // Load your patient list here
        patientNameBox.getItems().addAll("John Doe", "Jane Smith", "Alex Rivera");
    }

    @FXML
    private void handleNextStep() {
        String selectedPatient = patientNameBox.getValue();

        // 1. Validation
        if (selectedPatient == null || selectedPatient.isEmpty()) {
            showAlert("Input Required", "Please select a patient before proceeding.");
            return;
        }

        // 2. Store in AppContext
        AppContext context = AppContext.getInstance();
        context.setSelectedPatientName(selectedPatient);

        // 3. Navigation
        Stage stage = (Stage) patientNameBox.getScene().getWindow();
        Scene scene = sceneLoader.load(
                "popdiagnosis",
                "popdiagnosis-screen.fxml",
                "/styles/newdiagnosis.css" // Pass your branding here
        );

        if (scene == null) {
            showAlert("Navigation Error", "Could not load the upload screen.");
            return;
        }

        stage.setScene(scene);
        stage.setTitle("New Diagnosis - Upload Images");
        stage.show();
    }

    @FXML
    private void handleClose() {
        ((Stage) patientNameBox.getScene().getWindow()).close();
    }
}