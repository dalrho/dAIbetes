package org.example.daibetes.modules.doctor.ui.newdiagnosis;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.PatientSelectionDAO;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.ui.SceneLoader;

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
        String savedPatientName = AppContext.getInstance().getSelectedRecordsPatientName();

        if (savedPatientName == null || savedPatientName.isBlank()) {
            savedPatientName = AppContext.getInstance().getSelectedPatientName();
        }

        if (savedPatientName != null && patientNameBox.getItems().contains(savedPatientName)) {
            patientNameBox.setValue(savedPatientName);
        }
    }

    @FXML
    private void handleNextStep() {
        String selectedPatientName = patientNameBox.getValue();

        if (selectedPatientName == null || selectedPatientName.isBlank()) {
            showAlert("Input Required", "Please select a patient before proceeding.");
            return;
        }

        Integer selectedPatientId = patientMap.get(selectedPatientName);

        if (selectedPatientId == null) {
            showAlert("Selection Error", "Could not find the selected patient ID.");
            return;
        }

        AppContext context = AppContext.getInstance();

        int doctorId = context.getSelectedRecordsDoctorId();

        if (doctorId == 0) {
            User currentUser = context.getCurrentUser();

            if (currentUser instanceof Doctor doctor) {
                doctorId = doctor.getDId();
            }
        }

        if (doctorId == 0) {
            showAlert("Missing Doctor", "No logged-in doctor was found.");
            return;
        }

        /*
         * Store selected patient and doctor for GenerateResultsController.
         */
        context.setSelectedPatientId(selectedPatientId);
        context.setSelectedPatientName(selectedPatientName);

        context.setSelectedRecordsPatientId(selectedPatientId);
        context.setSelectedRecordsPatientName(selectedPatientName);
        context.setSelectedRecordsDoctorId(doctorId);

        /*
         * This is a new diagnosis, so clear old report selection.
         */
        context.setSelectedReportId(0);

        Stage stage = (Stage) patientNameBox.getScene().getWindow();

        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/doctor/ui/popup",
                "popdiagnosis-screen.fxml",
                "/org/example/daibetes/styles/new-diagnosis.css"
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