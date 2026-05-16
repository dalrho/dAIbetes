package patientsdashboard;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.Notification;
import register.sceneLoader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PatientDashboardController implements Initializable {

    @FXML private ImageView profileImage;
    @FXML private Label patientNameLabel;
    @FXML private Label patientIdLabel;
    @FXML private Button viewDiagnosisBtn;
    @FXML private Button scheduleFollowUpBtn;
    @FXML private Button myDoctorsBtn;
    @FXML private Button logoutBtn;

    @FXML private VBox recentActivityContainer;
    @FXML private Label daysUntilFollowUpLabel;
    @FXML private Label followUpDetailsLabel;
    @FXML private Label followUpDoctorLabel;

    @FXML private VBox medicationNotesContainer;
    @FXML private VBox scheduleContainer;

    private final PatientDashboardViewModel viewModel = new PatientDashboardViewModel();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Resolve static graphical elements
        URL imgUrl = getClass().getResource("/images/serato.jpg");
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

        // UI Core Bindings
        patientNameLabel.textProperty().bind(viewModel.patientNameProperty());
        patientIdLabel.textProperty().bind(viewModel.patientIdProperty());
        daysUntilFollowUpLabel.textProperty().bind(viewModel.daysUntilFollowUpProperty());
        followUpDetailsLabel.textProperty().bind(viewModel.followUpDetailsProperty());
        followUpDoctorLabel.textProperty().bind(viewModel.followUpDoctorProperty());

        // Attach structural node update observers
        viewModel.getDiagnoses().addListener((javafx.collections.ListChangeListener<String[]>) c -> populateMedicationNotes());
        viewModel.getNotifications().addListener((javafx.collections.ListChangeListener<Notification>) c -> populateRecentActivity());
        viewModel.getAcceptedSchedule().addListener((javafx.collections.ListChangeListener<String[]>) c -> populateScheduleBullets());

        // Perform foundational initial rendering loop
        populateMedicationNotes();
        populateRecentActivity();
        populateScheduleBullets();
        setupTooltips();
    }

    private void populateRecentActivity() {
        recentActivityContainer.getChildren().clear();

        if (viewModel.getNotifications().isEmpty()) {
            Label blankLabel = new Label("• No current notification updates available.");
            blankLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
            recentActivityContainer.getChildren().add(blankLabel);
            return;
        }

        for (Notification notif : viewModel.getNotifications()) {
            HBox layoutRow = new HBox(4);
            layoutRow.setAlignment(javafx.geometry.Pos.TOP_LEFT);

            Label explicitBullet = new Label("• ");
            explicitBullet.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #7B2528;");

            Hyperlink actionLink = new Hyperlink(notif.getMessage());
            actionLink.setWrapText(true);
            actionLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #111111; -fx-underline: false; -fx-padding: 0;");

            if ("RESCHEDULE".equalsIgnoreCase(notif.getActionType())) {
                actionLink.setStyle("-fx-font-size: 13px; -fx-text-fill: #C0392B; -fx-padding: 0;");
            }

            actionLink.setOnAction(e -> {
                Stage hostStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                if ("GO_TO_CALENDAR".equalsIgnoreCase(notif.getActionType()) || "RESCHEDULE".equalsIgnoreCase(notif.getActionType())) {
                    routeToCalendarScreen(hostStage);
                } else if ("VIEW_DIAGNOSIS".equalsIgnoreCase(notif.getActionType())) {
                    routeToRecordsScreen(hostStage);
                }
            });

            layoutRow.getChildren().addAll(explicitBullet, actionLink);
            recentActivityContainer.getChildren().add(layoutRow);
        }
    }

    private void populateMedicationNotes() {
        medicationNotesContainer.getChildren().clear();
        List<String[]> diagnoses = viewModel.getDiagnoses();
        int limit = Math.min(3, diagnoses.size());

        for (int i = 0; i < limit; i++) {
            String[] row = diagnoses.get(i);
            String noteText = (row[3] != null && !row[3].isEmpty()) ? row[3] : "No specific notes.";

            Label medLabel = new Label(noteText);
            medLabel.setWrapText(true);
            medLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333; -fx-line-spacing: 3;");
            medLabel.setPadding(new javafx.geometry.Insets(8, 0, 8, 0));

            medicationNotesContainer.getChildren().add(medLabel);
        }

        if (diagnoses.isEmpty()) {
            medicationNotesContainer.getChildren().add(createStyledEmptyLabel("No medication notes recorded."));
        }
    }

    private void populateScheduleBullets() {
        scheduleContainer.getChildren().clear();

        if (viewModel.getAcceptedSchedule().isEmpty()) {
            Label fallbackLabel = new Label("• No upcoming accepted medical consultations found.");
            fallbackLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");
            scheduleContainer.getChildren().add(fallbackLabel);
            return;
        }

        for (String[] row : viewModel.getAcceptedSchedule()) {
            HBox alignmentBox = new HBox(8);
            alignmentBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Circle bulletMarker = new Circle(4);
            bulletMarker.setStyle("-fx-fill: #27AE60;");

            Label informationalLabel = new Label("Confirmed appointment with Dr. " + row[1] + " scheduled on " + row[2]);
            informationalLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            informationalLabel.setWrapText(true);

            alignmentBox.getChildren().addAll(bulletMarker, informationalLabel);
            scheduleContainer.getChildren().add(alignmentBox);
        }
    }

    private Label createStyledEmptyLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #888888; -fx-font-size: 13px;");
        lbl.setWrapText(true);
        return lbl;
    }

    private void setupTooltips() {
        viewDiagnosisBtn.setTooltip(new Tooltip("View your current diagnoses and medical history"));
        scheduleFollowUpBtn.setTooltip(new Tooltip("Schedule or reschedule follow-up appointments"));
        myDoctorsBtn.setTooltip(new Tooltip("View your assigned doctors and specialists"));
    }

    private void routeToCalendarScreen(Stage stage) {
        Scene s = sceneLoader.load("patientcalendar", "patient-calendar.fxml", null);
        if (s != null) {
            stage.setScene(s);
            stage.setTitle("dAIbetes — Schedule Follow-up");
        }
    }

    private void routeToRecordsScreen(Stage stage) {
        Scene s = sceneLoader.load("recordsPatient", "records-screen-patient.fxml", null);
        if (s != null) {
            stage.setScene(s);
            stage.setTitle("dAIbetes — Patient Records");
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
    private void onMyDoctors(ActionEvent event) {
        // Retained original modal action pipeline handler logic structures
        System.out.println("Opening My Doctors pipeline...");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        AppContext.getInstance().clearSession();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(sceneLoader.load("splashscreen", "splash-screen.fxml", "/styles/splash.css"));
    }

    public void refreshDashboard() {
        viewModel.refreshDashboardData();
        populateMedicationNotes();
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