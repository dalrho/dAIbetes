package org.example.daibetes.modules.doctor.ui.newdiagnosis;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.PatientSelectionDAO;
import register.sceneLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

public class NewDiagnosisPopupController {

    @FXML private ComboBox<String> patientNameBox;

    private final Map<String, Integer> patientMap = new HashMap<>();

    @FXML
    public void initialize() {
        loadPatientsFromDatabase();
        restoreSelectedPatient();
    }

    private void loadPatientsFromDatabase() {
        PatientSelectionDAO dao = new PatientSelectionDAO();
        List<String[]> patients = dao.getAllPatients();

        patientNameBox.getItems().clear();
        patientMap.clear();

        for (String[] patient : patients) {
            int patientId = Integer.parseInt(patient[0]);
            String patientName = patient[1];

            patientNameBox.getItems().add(patientName);
            patientMap.put(patientName, patientId);
        }
    }

    private void restoreSelectedPatient() {
        String savedPatientName = AppContext.getInstance().getSelectedPatientName();

        if (savedPatientName != null && patientNameBox.getItems().contains(savedPatientName)) {
            patientNameBox.setValue(savedPatientName);
        }
    }

    @FXML
    private void handleNextStep() {
        String selectedPatient = patientNameBox.getValue();

        if (selectedPatient == null || selectedPatient.isBlank()) {
            showAlert("Input Required", "Please select a patient before proceeding.");
            return;
        }

        Integer selectedPatientId = patientMap.get(selectedPatient);

        if (selectedPatientId == null) {
            showAlert("Selection Error", "Could not find the selected patient ID.");
            return;
        }

        AppContext context = AppContext.getInstance();
        context.setSelectedPatientId(selectedPatientId);
        context.setSelectedPatientName(selectedPatient);

        Stage stage = (Stage) patientNameBox.getScene().getWindow();

        Scene scene = sceneLoader.load(
                "popdiagnosis",
                "popdiagnosis-screen.fxml",
                "/styles/newdiagnosis.css"
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