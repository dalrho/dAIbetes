package org.example.daibetes.modules.doctor.ui;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.core.database.DoctorDashboardDAO;
import org.example.daibetes.shared.utils.ValidationUtils;
import register.sceneLoader;

import java.util.List;

public class DoctorDashboardController {

    @FXML private ImageView profileImage;
    @FXML private Pane gaugePane;
    @FXML private Label doctorNameLabel;
    @FXML private Label recordsStatusLabel;
    @FXML private Label diagnosesStatusLabel;
    @FXML private Label patientsStatusLabel;
    @FXML private Label totalScansLabel;
    @FXML private Label toReviewLabel;
    @FXML private VBox recentActivitiesContainer;
    @FXML private VBox scheduleContainer;

    private int loggedInDoctorId = 1;

    private final DoctorDashboardDAO dashboardDAO = new DoctorDashboardDAO();

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    public void setLoggedInDoctorId(int doctorId) {
        this.loggedInDoctorId = doctorId;
        loadDashboardData();
    }

    private void loadDashboardData() {
        try {
            String doctorName = dashboardDAO.getDoctorName(loggedInDoctorId);
            int totalScans = dashboardDAO.getTotalScans(loggedInDoctorId);
            int toReview = dashboardDAO.getTestsWithoutDiagnosis(loggedInDoctorId);

            doctorNameLabel.setText("Dr. " + doctorName);

            totalScansLabel.setText(String.valueOf(totalScans));
            toReviewLabel.setText(String.valueOf(toReview));

            recordsStatusLabel.setText("You have " + totalScans + " total scan record(s).");
            diagnosesStatusLabel.setText(toReview + " scan(s) still need diagnosis.");
            patientsStatusLabel.setText("Recent opened patient diagnoses are shown below.");

            loadRecentActivities();
            loadSchedulePlaceholder();

        } catch (Exception e) {
            e.printStackTrace();

            doctorNameLabel.setText("Dr. Doctor");
            totalScansLabel.setText("0");
            toReviewLabel.setText("0");

            recordsStatusLabel.setText("Unable to load scan records.");
            diagnosesStatusLabel.setText("Unable to load review count.");
            patientsStatusLabel.setText("Unable to load recent activities.");

            recentActivitiesContainer.getChildren().clear();
            recentActivitiesContainer.getChildren().add(new Label("No recent activities loaded."));

            scheduleContainer.getChildren().clear();
            scheduleContainer.getChildren().add(new Label("No schedule available."));
        }
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
        /*NavigationUtils.switchScene(
                (javafx.scene.Node) event.getSource(),
                "/test/test.fxml",
                "New Diagnosis"
        );

         */
    }

    @FXML
    private void onViewPatients(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load("records", "records-screen.fxml", null)
        );

        stage.setTitle("dAIbetes — View Patients");
        stage.show();


    }

    @FXML
    private void onViewReports(ActionEvent event) {
        /*
        NavigationUtils.switchScene(
                (javafx.scene.Node) event.getSource(),
                "/results/results.fxml",
                "Reports"
        );

         */
    }

    @FXML
    private void onLogout(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load("login", "login-screen.fxml", "/styles/splash.css")
        );

        stage.setTitle("dAIbetes — Login");
        stage.show();
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

}