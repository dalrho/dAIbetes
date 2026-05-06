package patientsdashboard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import org.example.daibetes.core.domain.Patient;
import register.sceneLoader;

public class patientsdashboardController implements Initializable {
    // ════════════════════════════════════════════════════════════
    // FXML COMPONENTS - LEFT SIDEBAR
    // ════════════════════════════════════════════════════════════

    @FXML private ImageView profileImage;
    @FXML private Label patientNameLabel;
    @FXML private Label patientIdLabel;

    @FXML private Button viewDiagnosisBtn;
    @FXML private Button scheduleFollowUpBtn;
    @FXML private Button myDoctorsBtn;

    // ════════════════════════════════════════════════════════════
    // FXML COMPONENTS - MAIN CONTENT
    // ════════════════════════════════════════════════════════════

    @FXML private Button logoutBtn;

    // Recent Visits Section
    @FXML private VBox recentVisitsContainer;
    @FXML private Button viewAllVisitsBtn;

    // Days Until Follow-up Section
    @FXML private Label daysUntilFollowUpLabel;
    @FXML private Label followUpDetailsLabel;
    @FXML private Label followUpDoctorLabel;

    // Medication Notes Section
    @FXML private VBox medicationNotesContainer;

    // Schedule Section
    @FXML private VBox scheduleContainer;

    // Inbox Button
    @FXML private Button inboxBtn;

    // ════════════════════════════════════════════════════════════
    // INITIALIZATION
    // ════════════════════════════════════════════════════════════

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        URL imgUrl = getClass().getResource("/images/serato.jpg");

        if (imgUrl == null) {
            System.out.println("Image NOT FOUND in resources!");
        } else {
            Image img = new Image(imgUrl.toExternalForm());
            profileImage.setImage(img);
        }

        initializePatientData();
        populateRecentVisits();
        populateMedicationNotes();
        populateSchedule();
        calculateDaysUntilFollowUp();
        setupTooltips();
    }

    /**
     * Initialize patient-specific data
     */
    private void initializePatientData() {
        // Set patient name and ID - Replace with actual data from database/API
        patientNameLabel.setText("John Doe");
        patientIdLabel.setText("ID: 12345");

        // Load profile image if available
        try {
            // profileImage.setImage(new Image("path/to/profile/image.jpg"));
        } catch (Exception e) {
            System.out.println("Profile image not found: " + e.getMessage());
        }
    }

    /**
     * Populate recent visits container with patient's recent clinic visits
     */
    private void populateRecentVisits() {
        recentVisitsContainer.getChildren().clear();

        // Example data - replace with actual data from database
        String[] visits = {
                "Recardo Dalisay, Patient #3142 | January 12, Diagnosis | Pending"
        };

        for (String visit : visits) {
            VBox visitItem = createVisitItem(visit);
            recentVisitsContainer.getChildren().add(visitItem);
        }
    }

    /**
     * Create a styled visit item component
     */
    private VBox createVisitItem(String visitInfo) {
        VBox visitBox = new VBox();
        visitBox.setStyle("-fx-padding: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 8;");

        String[] parts = visitInfo.split("\\|");

        Label doctorLabel = new Label(parts[0].trim());
        doctorLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

        Label dateLabel = new Label(parts.length > 1 ? parts[1].trim() : "");
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-padding: 4 0 0 0;");

        Label statusLabel = new Label(parts.length > 2 ? parts[2].trim() : "");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #F39C12; -fx-padding: 4 0 0 0;");

        visitBox.getChildren().addAll(doctorLabel, dateLabel, statusLabel);
        return visitBox;
    }

    /**
     * Calculate and display days until next follow-up appointment
     */
    private void calculateDaysUntilFollowUp() {
        // Example: Follow-up date is 5 days from today
        // Replace with actual follow-up date from database
        LocalDate followUpDate = LocalDate.now().plusDays(5);
        LocalDate today = LocalDate.now();

        long daysUntil = ChronoUnit.DAYS.between(today, followUpDate);

        daysUntilFollowUpLabel.setText(String.valueOf(Math.max(0, daysUntil)));
        followUpDetailsLabel.setText("Follow up checkup");
        followUpDoctorLabel.setText("Appointment with Doctor Trush");
    }

    /**
     * Populate medication notes container with patient's current medications
     */
    private void populateMedicationNotes() {
        medicationNotesContainer.getChildren().clear();

        // Example medication data - replace with actual data from database
        String[] medications = {
                "Metformin (500mg): Take 1 tablet twice daily with meals to improve glycemic control.",
                "Lisinopril (10mg): Take 1 tablet every morning; monitor for dizziness or persistent cough.",
                "Atorvastatin (20mg): Take 1 tablet before bedtime; report any unusual muscle pain immediately.",
                "Emergency Note: Keep Glucose Tablets or a quick-sugar source available for instances of hypoglycemia."
        };

        for (String medication : medications) {
            Label medLabel = new Label(medication);
            medLabel.setWrapText(true);
            medLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333; -fx-line-spacing: 3;");
            medLabel.setPadding(new Insets(8, 0, 8, 0));

            medicationNotesContainer.getChildren().add(medLabel);
        }
    }

    /**
     * Populate schedule container with upcoming appointments
     */
    private void populateSchedule() {
        scheduleContainer.getChildren().clear();

        // Example schedule data - replace with actual data from database
        String[][] scheduleItems = {
                {"Monday - 2pm", "Follow up check up"},
                {"Tuesday - 2pm", "Monthly Check Up"}
        };

        for (String[] item : scheduleItems) {
            VBox scheduleItem = createScheduleItem(item[0], item[1]);
            scheduleContainer.getChildren().add(scheduleItem);
        }
    }

    /**
     * Create a styled schedule item component
     */
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

    /**
     * Setup tooltips for buttons
     */
    private void setupTooltips() {
        viewDiagnosisBtn.setTooltip(new Tooltip("View your current diagnoses and medical history"));
        scheduleFollowUpBtn.setTooltip(new Tooltip("Schedule or reschedule follow-up appointments"));
        myDoctorsBtn.setTooltip(new Tooltip("View your assigned doctors and specialists"));
        inboxBtn.setTooltip(new Tooltip("Check your messages from doctors and clinic"));
    }

    // ════════════════════════════════════════════════════════════
    // BUTTON ACTION HANDLERS
    // ════════════════════════════════════════════════════════════

    /**
     * Handle View Diagnosis button click
     */
    @FXML
    private void onViewDiagnosis() {
        System.out.println("View Diagnosis clicked");
        // Implement navigation to diagnosis view
        showNotification("Navigating to Diagnosis view...");
    }

    /**
     * Handle Schedule Follow-up button click
     */
    @FXML
    private void onScheduleFollowUp() {
        System.out.println("Schedule Follow-up clicked");
        // Implement navigation to appointment scheduling
        showNotification("Opening appointment scheduler...");
    }

    /**
     * Handle My Doctors button click
     */
    @FXML
    private void onMyDoctors() {
        System.out.println("My Doctors clicked");
        // Implement navigation to doctors list
        showNotification("Loading your doctors...");
    }

    /**
     * Handle View All Visits button click
     */
    @FXML
    private void onViewAllVisits() {
        System.out.println("View All Visits clicked");
        // Implement navigation to complete visits history
        showNotification("Loading all visits...");
    }

    /**
     * Handle Inbox button click
     */
    @FXML
    private void onInbox() {
        System.out.println("Inbox clicked");
        // Implement navigation to inbox
        showNotification("Opening Inbox...");
    }

    /**
     * Handle Logout button click
     */
    @FXML
    private void onLogout(ActionEvent event) {
        System.out.println("[Dashboard] Log out");

        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

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

    /**
     * Show a notification to the user
     */
    private void showNotification(String message) {
        System.out.println("Notification: " + message);
        // TODO: Implement proper notification/alert system
    }

    /**
     * Confirm logout action with user
     */
    private boolean confirmLogout() {
        // TODO: Implement confirmation dialog
        System.out.println("Logout confirmation requested");
        return true;
    }

    /**
     * Perform logout operation
     */
    private void performLogout() {
        System.out.println("Performing logout...");
        // TODO: Clear session data, close database connections, etc.
        // Get the stage and close the application
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.close();
    }

    /**
     * Refresh all dashboard data
     */
    public void refreshDashboard() {
        populateRecentVisits();
        populateMedicationNotes();
        populateSchedule();
        calculateDaysUntilFollowUp();
    }
}