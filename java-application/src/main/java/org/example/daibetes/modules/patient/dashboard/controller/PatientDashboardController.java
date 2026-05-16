package org.example.daibetes.modules.patient.dashboard.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.models.Notification;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.modules.patient.dashboard.model.PatientDashboardViewModel;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PatientDashboardController implements Initializable {

    @FXML private ImageView profileImage;
    @FXML private Label patientNameLabel;
    @FXML private Button viewDiagnosisBtn;
    @FXML private Button scheduleFollowUpBtn;
    @FXML private Button logoutBtn;

    @FXML private VBox recentActivityContainer;
    @FXML private Label daysUntilFollowUpLabel;
    @FXML private Label followUpDetailsLabel;
    @FXML private Label followUpDoctorLabel;

    @FXML private VBox scheduleContainer;

    private final PatientDashboardViewModel viewModel = new PatientDashboardViewModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL imgUrl = getClass().getResource("/org/example/daibetes/images/serato.jpg");
        if (imgUrl != null) {
            profileImage.setImage(new Image(imgUrl.toExternalForm()));
        }

        User currentUser = AppContext.getInstance().getCurrentUser();
        if (!(currentUser instanceof Patient)) {
            System.err.println("ERROR: Current user is not a Patient.");
            return;
        }

        Patient patient = (Patient) currentUser;
        viewModel.initData(patient);

        // Functional Context Bindings
        patientNameLabel.textProperty().bind(viewModel.patientNameProperty());
        daysUntilFollowUpLabel.textProperty().bind(viewModel.daysUntilFollowUpProperty());
        followUpDetailsLabel.textProperty().bind(viewModel.followUpDetailsProperty());
        followUpDoctorLabel.textProperty().bind(viewModel.followUpDoctorProperty());

        // Attach structural collections synchronization routines
        viewModel.getNotifications().addListener((javafx.collections.ListChangeListener<Notification>) c -> populateRecentActivity());
        viewModel.getAcceptedSchedule().addListener((javafx.collections.ListChangeListener<String[]>) c -> populateScheduleBullets());

        // Initial paint execution loops
        populateRecentActivity();
        populateScheduleBullets();
        setupTooltips();
    }

    private void populateRecentActivity() {
        recentActivityContainer.getChildren().clear();

        if (viewModel.getNotifications().isEmpty()) {
            Label placeholder = new Label("• No current notification updates available.");
            placeholder.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
            recentActivityContainer.getChildren().add(placeholder);
            return;
        }

        for (Notification notif : viewModel.getNotifications()) {
            HBox renderingBox = new HBox(4);
            renderingBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);

            Label structuralBullet = new Label("• ");
            structuralBullet.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #7B2528;");

            Hyperlink dynamicLink = new Hyperlink(notif.getMessage());
            dynamicLink.setWrapText(true);
            dynamicLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #111111; -fx-underline: false; -fx-padding: 0;");

            if ("RESCHEDULE".equalsIgnoreCase(notif.getActionType())) {
                dynamicLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #C0392B; -fx-padding: 0;");
            }

            dynamicLink.setOnAction(e -> {
                Stage currentWindow = (Stage) ((Node) e.getSource()).getScene().getWindow();
                if ("GO_TO_CALENDAR".equalsIgnoreCase(notif.getActionType())) {
                    routeToCalendarScreen(currentWindow);
                } else if ("VIEW_DIAGNOSIS".equalsIgnoreCase(notif.getActionType())) {
                    routeToRecordsScreen(currentWindow);
                }
            });

            renderingBox.getChildren().addAll(structuralBullet, dynamicLink);
            recentActivityContainer.getChildren().add(renderingBox);
        }
    }

    private void populateScheduleBullets() {
        scheduleContainer.getChildren().clear();

        if (viewModel.getAcceptedSchedule().isEmpty()) {
            Label fallback = new Label("• No upcoming accepted medical consultations found.");
            fallback.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
            scheduleContainer.getChildren().add(fallback);
            return;
        }

        for (String[] row : viewModel.getAcceptedSchedule()) {
            HBox bulletLayout = new HBox(8);
            bulletLayout.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Circle bulletPoint = new Circle(4);
            bulletPoint.setStyle("-fx-fill: #27AE60;");

            Label contentDescription = new Label("Confirmed appointment with Dr. " + row[1] + " scheduled on " + row[2]);
            contentDescription.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            contentDescription.setWrapText(true);

            bulletLayout.getChildren().addAll(bulletPoint, contentDescription);
            scheduleContainer.getChildren().add(bulletLayout);
        }
    }

    private void setupTooltips() {
        viewDiagnosisBtn.setTooltip(new Tooltip("View your current diagnoses and medical history"));
        scheduleFollowUpBtn.setTooltip(new Tooltip("Schedule or reschedule follow-up appointments"));
    }

    private void routeToCalendarScreen(Stage stage) {
        Scene s = SceneLoader.load("org/example/daibetes/modules/patient/ui/calendar", "patient-calendar.fxml", null);
        if (s != null) {
            stage.setScene(s);
            stage.setTitle("dAIbetes — Consultation Calendar");
        }
    }

    private void routeToRecordsScreen(Stage stage) {
        Scene s = SceneLoader.load("org/example/daibetes/modules/records/controller", "records-screen-patient.fxml", null);
        if (s != null) {
            stage.setScene(s);
            stage.setTitle("dAIbetes — Medical Records");
        }
    }

    @FXML
    private void onViewDiagnosis(ActionEvent event) {
        routeToRecordsScreen((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    @FXML
    private void onScheduleFollowUp(ActionEvent event) {
        routeToCalendarScreen((Stage) ((Node) event.getSource()).getScene().getWindow());
    }

    @FXML
    private void onLogout(ActionEvent event) {
        AppContext.getInstance().clearSession();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SceneLoader.load("org/example/daibetes/modules/splash/controller", "splash-screen.fxml", "/org/example/daibetes/styles/splash.css"));
    }

    public void refreshDashboard() {
        viewModel.refreshDashboardData();
    }

    private void populateSearchResults(VBox container, Stage modal) {
        container.getChildren().clear();
        for (Doctor doctor : viewModel.getSearchResults()) {
            HBox row = new HBox(12);
            row.setStyle("-fx-background-color: white; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand;");
            VBox info = new VBox(2);

            Label nameLbl = new Label("Dr. " + doctor.getFirstname() + " " + doctor.getLastname());
            nameLbl.setStyle("-fx-text-fill: #111111; -fx-font-size: 14px;");
            Label hospLbl = new Label(doctor.getHospital());
            hospLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");

            info.getChildren().addAll(nameLbl, hospLbl);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button requestBtn = new Button("REQUEST");
            requestBtn.setStyle("-fx-background-color: #7B2528; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 6;");
            requestBtn.setOnAction(e -> {
                viewModel.selectDoctor(doctor);
                handleImageUploadAndRequest(modal);
            });
            row.getChildren().addAll(info, spacer, requestBtn);
            container.getChildren().add(row);
        }
    }

    private void handleImageUploadAndRequest(Stage modal) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Upload Retinal Scan");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));
        File file = fc.showOpenDialog(modal);
        if (file == null) return;

        ImageDAO imageDAO = new ImageDAO();
        int rawImageId = imageDAO.createImage(file, 1);

        viewModel.requestTest(rawImageId);
        if (viewModel.requestSuccessProperty().get()) {
            modal.close();
            viewModel.refreshDashboardData();
        }
    }
}