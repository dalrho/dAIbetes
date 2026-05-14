package patientsdashboard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;
import register.sceneLoader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for patients-dashboard.fxml.
 * Implements Initializable to match the existing FXML contract.
 * Reads the logged-in patient from AppContext — no initData() needed.
 */
public class PatientDashboardController implements Initializable {

    // ════════════════════════════════════════════════════════════
    // FXML COMPONENTS - LEFT SIDEBAR
    // ════════════════════════════════════════════════════════════

    @FXML private ImageView profileImage;
    @FXML private Label     patientNameLabel;
    @FXML private Label     patientIdLabel;
    @FXML private Button    viewDiagnosisBtn;
    @FXML private Button    scheduleFollowUpBtn;
    @FXML private Button    myDoctorsBtn;

    // ════════════════════════════════════════════════════════════
    // FXML COMPONENTS - MAIN CONTENT
    // ════════════════════════════════════════════════════════════

    @FXML private Button logoutBtn;

    @FXML private VBox   recentVisitsContainer;
    @FXML private Button viewAllVisitsBtn;

    @FXML private Label  daysUntilFollowUpLabel;
    @FXML private Label  followUpDetailsLabel;
    @FXML private Label  followUpDoctorLabel;

    @FXML private VBox   medicationNotesContainer;
    @FXML private VBox   scheduleContainer;

    @FXML private Button inboxBtn;

    private final PatientDashboardViewModel viewModel = new PatientDashboardViewModel();

    // ════════════════════════════════════════════════════════════
    // INITIALIZATION
    // ════════════════════════════════════════════════════════════

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Load profile image
        URL imgUrl = getClass().getResource("/images/serato.jpg");
        if (imgUrl != null) {
            profileImage.setImage(new Image(imgUrl.toExternalForm()));
        } else {
            System.out.println("Profile image not found in resources.");
        }

        // Resolve logged-in patient from session
        User currentUser = AppContext.getInstance().getCurrentUser();

        if (!(currentUser instanceof Patient)) {
            System.err.println("ERROR: Current user is not a Patient.");
            return;
        }

        Patient patient = (Patient) currentUser;
        viewModel.initData(patient);

        // --- Bind profile labels ---
        patientNameLabel.textProperty().bind(viewModel.patientNameProperty());
        patientIdLabel.textProperty().bind(viewModel.patientIdProperty());

        // --- Bind follow-up card ---
        daysUntilFollowUpLabel.textProperty().bind(viewModel.daysUntilFollowUpProperty());
        followUpDetailsLabel.textProperty().bind(viewModel.followUpDetailsProperty());
        followUpDoctorLabel.textProperty().bind(viewModel.followUpDoctorProperty());

        // --- Populate VBox containers reactively ---
        viewModel.getDiagnoses().addListener(
                (javafx.collections.ListChangeListener<String[]>) c -> {
                    populateRecentVisits();
                    populateMedicationNotes();
                });

        viewModel.getSchedule().addListener(
                (javafx.collections.ListChangeListener<String[]>) c ->
                        populateSchedule());

        // Initial population
        populateRecentVisits();
        populateMedicationNotes();
        populateSchedule();

        setupTooltips();
    }

    // ════════════════════════════════════════════════════════════
    // POPULATE METHODS
    // ════════════════════════════════════════════════════════════

    private void populateRecentVisits() {
        recentVisitsContainer.getChildren().clear();
        List<String[]> diagnoses = viewModel.getDiagnoses();
        int limit = Math.min(3, diagnoses.size());

        for (int i = 0; i < limit; i++) {
            String[] row = diagnoses.get(i);
            // row: [0]=id [1]=doctor [2]=diagnosis [3]=recommendation [4]=date
            recentVisitsContainer.getChildren().add(
                    createVisitItem("Dr. " + row[1], row[4], row[2]));
        }

        if (diagnoses.isEmpty()) {
            recentVisitsContainer.getChildren().add(
                    styledLabel("No recent visits.", "#888888", "13px"));
        }
    }

    private VBox createVisitItem(String doctorName, String date, String diagnosisText) {
        VBox visitBox = new VBox();
        visitBox.setStyle("-fx-padding: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 8;");

        Label doctorLabel = new Label(doctorName);
        doctorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

        Label dateLabel = new Label(date);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-padding: 4 0 0 0;");

        Label diagLabel = new Label(truncate(diagnosisText, 60));
        diagLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #F39C12; -fx-padding: 4 0 0 0;");
        diagLabel.setWrapText(true);

        visitBox.getChildren().addAll(doctorLabel, dateLabel, diagLabel);
        return visitBox;
    }

    private void populateMedicationNotes() {
        medicationNotesContainer.getChildren().clear();
        List<String[]> diagnoses = viewModel.getDiagnoses();
        int limit = Math.min(3, diagnoses.size());

        for (int i = 0; i < limit; i++) {
            String[] row = diagnoses.get(i);
            String rec = (row[3] != null && !row[3].isEmpty()) ? row[3] : "No specific notes.";

            Label medLabel = new Label(rec);
            medLabel.setWrapText(true);
            medLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333; -fx-line-spacing: 3;");
            medLabel.setPadding(new javafx.geometry.Insets(8, 0, 8, 0));

            medicationNotesContainer.getChildren().add(medLabel);
        }

        if (diagnoses.isEmpty()) {
            medicationNotesContainer.getChildren().add(
                    styledLabel("No medication notes.", "#888888", "13px"));
        }
    }

    private void populateSchedule() {
        scheduleContainer.getChildren().clear();
        List<String[]> schedule = viewModel.getSchedule();
        boolean hasPending = false;

        for (String[] row : schedule) {
            // row: [0]=test_id [1]=doctor [2]=date [3]=status
            if ("Pending".equals(row[3])) {
                scheduleContainer.getChildren().add(
                        createScheduleItem(row[2], "Dr. " + row[1]));
                hasPending = true;
            }
        }

        if (!hasPending) {
            scheduleContainer.getChildren().add(
                    styledLabel("No upcoming tests.", "#888888", "13px"));
        }
    }

    private VBox createScheduleItem(String time, String description) {
        VBox scheduleBox = new VBox();
        scheduleBox.setSpacing(4);
        scheduleBox.setStyle("-fx-padding: 0;");

        HBox timeBox = new HBox();
        timeBox.setStyle("-fx-alignment: center-left; -fx-spacing: 8;");

        javafx.scene.shape.Circle dotCircle = new javafx.scene.shape.Circle(6);
        dotCircle.setStyle("-fx-fill: #F39C12;");

        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        timeBox.getChildren().addAll(dotCircle, timeLabel);

        Label descLabel = new Label(description);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999999; -fx-padding: 0 0 0 22;");

        scheduleBox.getChildren().addAll(timeBox, descLabel);
        return scheduleBox;
    }

    private void setupTooltips() {
        viewDiagnosisBtn.setTooltip(new Tooltip("View your current diagnoses and medical history"));
        scheduleFollowUpBtn.setTooltip(new Tooltip("Schedule or reschedule follow-up appointments"));
        myDoctorsBtn.setTooltip(new Tooltip("View your assigned doctors and specialists"));
        inboxBtn.setTooltip(new Tooltip("Check your messages from doctors and clinic"));
    }

    // ════════════════════════════════════════════════════════════
    // BUTTON ACTION HANDLERS
    // ════════════════════════════════════════════════════════════

    @FXML
    private void onViewDiagnosis() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("My Diagnoses");

        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 24; -fx-background-color: #F2F2F2;");
        content.getChildren().add(styledLabel("Diagnosis Records", "#111111", "18px"));

        List<String[]> diagnoses = viewModel.getDiagnoses();
        if (diagnoses.isEmpty()) {
            content.getChildren().add(styledLabel("No records found.", "#888888", "13px"));
        } else {
            for (String[] row : diagnoses) content.getChildren().add(buildDetailCard(row));
        }

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        modal.setScene(new Scene(scroll, 600, 500));
        modal.show();
    }

    @FXML
    private void onScheduleFollowUp(ActionEvent event) {
        Scene scene = sceneLoader.load(
                "patientcalendar",
                "patient-calendar.fxml",
                null
        );

        if (scene == null) {
            System.out.println("Failed to load Patient Calendar screen");
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Schedule Follow-up");
    }

    private void populateSearchResults(VBox container, Stage modal) {
        container.getChildren().clear();
        for (Doctor doctor : viewModel.getSearchResults()) {
            HBox row = new HBox(12);
            row.setStyle("-fx-background-color: white; -fx-padding: 12; " +
                    "-fx-background-radius: 8; -fx-cursor: hand;");

            VBox info = new VBox(2);
            info.getChildren().addAll(
                    styledLabel("Dr. " + doctor.getFirstname() + " " + doctor.getLastname(),
                            "#111111", "14px"),
                    styledLabel(doctor.getHospital(), "#666666", "12px")
            );

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button requestBtn = new Button("REQUEST");
            requestBtn.setStyle("-fx-background-color: #7B2528; -fx-text-fill: white; " +
                    "-fx-font-size: 12px; -fx-font-weight: bold; " +
                    "-fx-padding: 6 14; -fx-background-radius: 6;");
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));

        File file = fc.showOpenDialog(modal);
        if (file == null) return;

        ImageDAO imageDAO = new ImageDAO();
        int rawImageId = imageDAO.createImage(file, 1);

        viewModel.requestTest(rawImageId);

        if (viewModel.requestSuccessProperty().get()) {
            modal.close();
            populateSchedule();
        }
    }

    @FXML
    private void onMyDoctors() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("My Doctors");

        VBox content = new VBox(12);
        content.setStyle("-fx-padding: 24; -fx-background-color: #F2F2F2;");
        content.getChildren().add(styledLabel("My Doctors", "#111111", "18px"));

        List<Doctor> doctors = viewModel.getMyDoctors();
        if (doctors.isEmpty()) {
            content.getChildren().add(styledLabel("No doctors found.", "#888888", "13px"));
        } else {
            for (Doctor d : doctors) {
                HBox row = new HBox(14);
                row.setStyle("-fx-background-color: white; -fx-padding: 14; -fx-background-radius: 8;");
                VBox info = new VBox(3);
                info.getChildren().addAll(
                        styledLabel("Dr. " + d.getFirstname() + " " + d.getLastname(), "#111111", "14px"),
                        styledLabel(d.getHospital(), "#666666", "12px"),
                        styledLabel("License: " + d.getLicenseNumber(), "#999999", "11px")
                );
                row.getChildren().add(info);
                content.getChildren().add(row);
            }
        }

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        modal.setScene(new Scene(scroll, 480, 400));
        modal.show();
    }

    @FXML
    private void onViewAllVisits() {
        onViewDiagnosis();
    }

    @FXML
    private void onInbox() {
        // Placeholder — Inbox feature to be implemented in a future PR
        showNotification("Opening Inbox...");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        AppContext.getInstance().clearSession();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(
                sceneLoader.load(
                        "splashscreen",
                        "splash-screen.fxml",
                        "/styles/splash.css"
                )
        );
    }

    // ════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ════════════════════════════════════════════════════════════

    public void refreshDashboard() {
        populateRecentVisits();
        populateMedicationNotes();
        populateSchedule();
    }

    private void showNotification(String message) {
        System.out.println("Notification: " + message);
    }

    private VBox buildDetailCard(String[] row) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 16; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 8, 0, 0, 2);");
        card.getChildren().addAll(
                styledLabel("Dr. " + row[1], "#111111", "14px"),
                styledLabel(row[4], "#999999", "11px"),
                styledLabel("Diagnosis:", "#555555", "12px"),
                styledLabel(row[2], "#333333", "13px"),
                styledLabel("Recommendation:", "#555555", "12px"),
                styledLabel(row[3] != null ? row[3] : "—", "#333333", "13px")
        );
        return card;
    }

    private Label styledLabel(String text, String color, String size) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + ";");
        lbl.setWrapText(true);
        return lbl;
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }
}