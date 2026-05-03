package org.example.daibetes.modules.doctor.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.daibetes.core.database.DoctorDashboardDAO;
import org.example.daibetes.shared.utils.ValidationUtils;

public class DoctorDashboardController {

    @FXML private Label doctorNameLabel;
    @FXML private Label totalScansLabel;
    @FXML private Label toReviewLabel;
    @FXML private ListView<String> recentActivitiesList;

    // temp only, get this after login/session
    private int loggedInDoctorId = 1;

    private DoctorDashboardDAO dashboardDAO = new DoctorDashboardDAO();

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        String doctorName = dashboardDAO.getDoctorName(loggedInDoctorId);
        int totalScans = dashboardDAO.getTotalScans(loggedInDoctorId);
        int toReview = dashboardDAO.getTestsWithoutDiagnosis(loggedInDoctorId);

        doctorNameLabel.setText("Good morning,\nDoc. " + doctorName);
        totalScansLabel.setText(String.valueOf(totalScans));
        toReviewLabel.setText(String.valueOf(toReview));

        recentActivitiesList.getItems().clear();
        recentActivitiesList.getItems().addAll(
                dashboardDAO.getRecentViewedPatients(loggedInDoctorId)
        );
    }

    @FXML
    public void handleNewDiagnosis(ActionEvent event) {
        //not yet done
        ValidationUtils.showAlert("New Diagnosis", "Open new diagnosis form here.");
    }

    @FXML
    public void handleViewRecords(ActionEvent event) {
        //shows patients list
        //not yet done
        ValidationUtils.showAlert("View Patients", "Show all patients handled by this doctor.");
    }

    @FXML
    public void handleReports(ActionEvent event) {
        //shows the reports the doctor made, retrieved from tblDiagnosis
        //not yet done
        ValidationUtils.showAlert("View Reports", "Show all diagnoses/reports made by this doctor.");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        //redirects to log-in page
        ValidationUtils.showAlert("Logout", "Return to login page here.");
    }
}