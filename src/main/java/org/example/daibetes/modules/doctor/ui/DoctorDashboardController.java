package org.example.daibetes.modules.doctor.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.core.database.DoctorDashboardDAO;
import org.example.daibetes.shared.utils.ValidationUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class DoctorDashboardController {

    @FXML private ImageView profileImage;

    @FXML private Pane gaugePane;

    @FXML private Label recordsStatusLabel;
    @FXML private Label diagnosesStatusLabel;
    @FXML private Label patientsStatusLabel;

    @FXML private Label totalScansLabel;
    @FXML private Label toReviewLabel;

    @FXML private VBox recentActivitiesContainer;
    @FXML private VBox scheduleContainer;

    // temporary only, replace later with logged-in doctor ID
    private int loggedInDoctorId = 1;

    private final DoctorDashboardDAO dashboardDAO = new DoctorDashboardDAO();

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        int totalScans = dashboardDAO.getTotalScans(loggedInDoctorId);
        int toReview = dashboardDAO.getTestsWithoutDiagnosis(loggedInDoctorId);

        totalScansLabel.setText(String.valueOf(totalScans));
        toReviewLabel.setText(String.valueOf(toReview));

        recordsStatusLabel.setText("You have " + totalScans + " total scan record(s).");
        diagnosesStatusLabel.setText(toReview + " test(s) still need diagnosis.");
        patientsStatusLabel.setText("Recent patients are shown below.");

        loadRecentActivities();
        loadSchedulePlaceholder();
    }

    private void loadRecentActivities() {
        recentActivitiesContainer.getChildren().clear();

        List<String> activities = dashboardDAO.getRecentViewedPatients(loggedInDoctorId);

        if (activities.isEmpty()) {
            Label emptyLabel = new Label("No recent activities yet.");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #777777;");
            recentActivitiesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (String activity : activities) {
            Label activityLabel = new Label(activity);
            activityLabel.setWrapText(true);
            activityLabel.setStyle("""
                    -fx-font-size: 13px;
                    -fx-text-fill: #333333;
                    -fx-background-color: #F5F5F5;
                    -fx-padding: 10;
                    -fx-background-radius: 8;
                    """);

            recentActivitiesContainer.getChildren().add(activityLabel);
        }
    }

    private void loadSchedulePlaceholder() {
        scheduleContainer.getChildren().clear();

        Label scheduleLabel = new Label("No schedule available.");
        scheduleLabel.setWrapText(true);
        scheduleLabel.setStyle("""
                -fx-font-size: 13px;
                -fx-text-fill: #777777;
                -fx-background-color: #F5F5F5;
                -fx-padding: 10;
                -fx-background-radius: 8;
                """);

        scheduleContainer.getChildren().add(scheduleLabel);
    }

    @FXML
    private void onNewDiagnosis(ActionEvent event) {
        ValidationUtils.showAlert("placeholder","to be developed");
        //loadPage(event, "/test/test.fxml", "New Diagnosis");
    }

    @FXML
    private void onViewPatients(ActionEvent event) {
        loadPage(event, "/records/records.fxml", "Patient Records");
    }

    @FXML
    private void onViewReports(ActionEvent event) {
        ValidationUtils.showAlert("placeholder","to be developed");
        //loadPage(event, "/results/results.fxml", "Reports");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        loadPage(event, "/login/login-screen.fxml", "dAIbetes — Login");
    }

    @FXML
    private void onUpdateData(ActionEvent event) {
        loadDashboardData();
        ValidationUtils.showAlert("Updated", "Dashboard data has been refreshed.");
    }

    @FXML
    private void onInbox(ActionEvent event) {
        ValidationUtils.showAlert("Inbox", "Inbox page is not yet available.");
    }

    private void loadPage(ActionEvent event, String fxmlPath, String title) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                ValidationUtils.showAlert(
                        "Page Not Found",
                        "Cannot find: " + fxmlPath + "\n\nCheck if the FXML file exists in resources."
                );
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            ValidationUtils.showAlert("Navigation Error", "Unable to open " + title + " page.");
        }
    }
}