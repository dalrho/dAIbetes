package patientsdashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Controller for patients-dashboard-screen.fxml.
 * Package: patientsdashboard (matches fx:controller in FXML).
 *
 * fx:id mapping:
 *   profileImage            → patient avatar
 *   patientNameLabel        → patient full name
 *   patientIdLabel          → patient ID
 *   viewDiagnosisBtn        → sidebar: opens diagnosis overlay
 *   scheduleFollowUpBtn     → sidebar: opens request-scan overlay
 *   myDoctorsBtn            → sidebar: opens my doctors overlay
 *   logoutBtn               → top-right logout
 *   recentVisitsContainer   → VBox: last 3 diagnosis rows
 *   viewAllVisitsBtn        → opens all diagnoses overlay
 *   daysUntilFollowUpLabel  → countdown number
 *   followUpDetailsLabel    → "Follow-up on <date>"
 *   followUpDoctorLabel     → "Dr. <name>"
 *   medicationNotesContainer→ VBox: recommendation rows from latest diagnosis
 *   scheduleContainer       → VBox: upcoming test rows
 *   inboxBtn                → placeholder (future feature)
 */
public class PatientDashboardControllerTest {

    // --- Sidebar ---
    @FXML private ImageView profileImage;
    @FXML private Label     patientNameLabel;
    @FXML private Label     patientIdLabel;
    @FXML private Button    viewDiagnosisBtn;
    @FXML private Button    scheduleFollowUpBtn;
    @FXML private Button    myDoctorsBtn;

    // --- Top bar ---
    @FXML private Button    logoutBtn;

    // --- Dashboard cards ---
    @FXML private VBox      recentVisitsContainer;
    @FXML private Button    viewAllVisitsBtn;
    @FXML private Label     daysUntilFollowUpLabel;
    @FXML private Label     followUpDetailsLabel;
    @FXML private Label     followUpDoctorLabel;
    @FXML private VBox      medicationNotesContainer;
    @FXML private VBox      scheduleContainer;

    // --- Bottom ---
    @FXML private Button    inboxBtn;

    private final PatientDashboardViewModelTest viewModel = new PatientDashboardViewModelTest();

    // =========================================================================
    // Init — called by loginController after successful login
    // =========================================================================

    public void initData(Patient patient) {
        viewModel.initData(patient);
    }

    // =========================================================================
    // JavaFX initialize — bindings and listeners
    // =========================================================================

    @FXML
    public void initialize() {

        // --- Patient profile bindings ---
        patientNameLabel.textProperty().bind(viewModel.patientNameProperty());
        patientIdLabel.textProperty().bind(viewModel.patientIdProperty());

        // --- Follow-up card bindings ---
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
                (javafx.collections.ListChangeListener<String[]>) c -> populateScheduleCard());
    }

    // =========================================================================
    // VBox card population helpers
    // =========================================================================

    /**
     * Fills recentVisitsContainer with the 3 most recent diagnoses.
     * Each row: "Dr. <name>  |  <date>"
     */
    private void populateRecentVisits() {
        recentVisitsContainer.getChildren().clear();
        List<String[]> diagnoses = viewModel.getDiagnoses();

        int limit = Math.min(3, diagnoses.size());
        for (int i = 0; i < limit; i++) {
            String[] row = diagnoses.get(i);
            // row: [0]=id [1]=doctor [2]=diagnosis [3]=recommendation [4]=date
            recentVisitsContainer.getChildren().add(
                    buildVisitRow("Dr. " + row[1], row[4], row[2]));
        }

        if (diagnoses.isEmpty()) {
            recentVisitsContainer.getChildren().add(
                    styledLabel("No recent visits.", "#888888", "13px"));
        }
    }

    /**
     * Fills medicationNotesContainer with recommendations from the
     * 3 most recent diagnoses.
     */
    private void populateMedicationNotes() {
        medicationNotesContainer.getChildren().clear();
        List<String[]> diagnoses = viewModel.getDiagnoses();

        int limit = Math.min(3, diagnoses.size());
        for (int i = 0; i < limit; i++) {
            String[] row = diagnoses.get(i);
            String rec = row[3] != null && !row[3].isEmpty()
                    ? row[3] : "No specific notes.";
            medicationNotesContainer.getChildren().add(
                    buildNoteRow(row[4], rec));
        }

        if (diagnoses.isEmpty()) {
            medicationNotesContainer.getChildren().add(
                    styledLabel("No medication notes.", "#888888", "13px"));
        }
    }

    /**
     * Fills scheduleContainer with upcoming pending tests.
     */
    private void populateScheduleCard() {
        scheduleContainer.getChildren().clear();
        List<String[]> schedule = viewModel.getSchedule();

        boolean hasPending = false;
        for (String[] row : schedule) {
            // row: [0]=test_id [1]=doctor [2]=date [3]=status
            if ("Pending".equals(row[3])) {
                scheduleContainer.getChildren().add(
                        buildScheduleRow("Dr. " + row[1], row[2], row[3]));
                hasPending = true;
            }
        }

        if (!hasPending) {
            scheduleContainer.getChildren().add(
                    styledLabel("No upcoming tests.", "#888888", "13px"));
        }
    }

    // =========================================================================
    // Sidebar button handlers
    // =========================================================================

    /** VIEW DIAGNOSIS — opens a modal listing all diagnosis records. */
    @FXML
    private void onViewDiagnosis() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("My Diagnoses");

        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 24; -fx-background-color: #F2F2F2;");

        Label title = styledLabel("Diagnosis Records", "#111111", "18px");
        title.setStyle(title.getStyle() + "; -fx-font-weight: bold;");
        content.getChildren().add(title);

        List<String[]> diagnoses = viewModel.getDiagnoses();
        if (diagnoses.isEmpty()) {
            content.getChildren().add(styledLabel("No records found.", "#888888", "13px"));
        } else {
            for (String[] row : diagnoses) {
                content.getChildren().add(buildDetailCard(row));
            }
        }

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        modal.setScene(new Scene(scroll, 600, 500));
        modal.show();
    }

    /** SCHEDULE FOLLOW-UP — search for a doctor and submit a scan request. */
    @FXML
    private void onScheduleFollowUp() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("Request Scan");

        VBox content = new VBox(14);
        content.setStyle("-fx-padding: 24; -fx-background-color: #F2F2F2;");

        Label title = styledLabel("Request a Scan", "#111111", "18px");
        title.setStyle(title.getStyle() + "; -fx-font-weight: bold;");

        // Search row
        javafx.scene.control.TextField searchField = new javafx.scene.control.TextField();
        searchField.setPromptText("Search doctor name or hospital...");
        searchField.textProperty().bindBidirectional(viewModel.searchKeywordProperty());
        VBox searchResultsBox = new VBox(8);
        Button searchBtn = new Button("SEARCH");
        searchBtn.setStyle("-fx-background-color: #1A1A1A; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 6;");
        searchBtn.setOnAction(e -> {
            viewModel.search();
            populateSearchResults(searchResultsBox, modal);
        });

        HBox searchRow = new HBox(10, searchField, searchBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);



        // Status label
        Label statusLbl = new Label();
        statusLbl.textProperty().bind(viewModel.statusMessageProperty());
        statusLbl.setStyle("-fx-text-fill: #C0392B; -fx-font-size: 12px;");

        content.getChildren().addAll(title, searchRow, searchResultsBox, statusLbl);

        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(content);
        scroll.setFitToWidth(true);
        modal.setScene(new Scene(scroll, 560, 460));
        modal.show();
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
        int rawImageId = imageDAO.createImage(new File(file.getAbsolutePath()), 1);

        viewModel.requestTest(rawImageId);

        if (viewModel.requestSuccessProperty().get()) {
            modal.close();
            populateScheduleCard();
        }
    }

    /** MY DOCTORS — lists all doctors the patient has had tests with. */
    @FXML
    private void onMyDoctors() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.setTitle("My Doctors");

        VBox content = new VBox(12);
        content.setStyle("-fx-padding: 24; -fx-background-color: #F2F2F2;");

        Label title = styledLabel("My Doctors", "#111111", "18px");
        title.setStyle(title.getStyle() + "; -fx-font-weight: bold;");
        content.getChildren().add(title);

        List<Doctor> doctors = viewModel.getMyDoctors();
        if (doctors.isEmpty()) {
            content.getChildren().add(
                    styledLabel("No doctors found.", "#888888", "13px"));
        } else {
            for (Doctor d : doctors) {
                HBox row = new HBox(14);
                row.setStyle("-fx-background-color: white; -fx-padding: 14; " +
                        "-fx-background-radius: 8;");
                VBox info = new VBox(3);
                info.getChildren().addAll(
                        styledLabel("Dr. " + d.getFirstname() + " " + d.getLastname(),
                                "#111111", "14px"),
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

    /** VIEW ALL VISITS — same as onViewDiagnosis, shows all records. */
    @FXML
    private void onViewAllVisits() {
        onViewDiagnosis();
    }

    // =========================================================================
    // Other handlers
    // =========================================================================

    @FXML
    private void onLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/login/login-screen.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onInbox() {
        // Placeholder — Inbox feature to be implemented in a future PR
    }

    // =========================================================================
    // UI builder helpers
    // =========================================================================

    private HBox buildVisitRow(String doctorName, String date, String diagnosisText) {
        HBox row = new HBox(12);
        row.setStyle("-fx-background-color: #F8F8F8; -fx-padding: 12; " +
                "-fx-background-radius: 8;");

        VBox info = new VBox(3);
        info.getChildren().addAll(
                styledLabel(doctorName, "#111111", "14px"),
                styledLabel(truncate(diagnosisText, 60), "#666666", "12px")
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLbl = styledLabel(date, "#999999", "12px");
        row.getChildren().addAll(info, spacer, dateLbl);
        return row;
    }

    private VBox buildNoteRow(String date, String note) {
        VBox box = new VBox(4);
        box.setStyle("-fx-background-color: #F8F8F8; -fx-padding: 12; " +
                "-fx-background-radius: 8;");
        box.getChildren().addAll(
                styledLabel(date, "#999999", "11px"),
                styledLabel(truncate(note, 80), "#333333", "13px")
        );
        return box;
    }

    private HBox buildScheduleRow(String doctorName, String date, String status) {
        HBox row = new HBox(12);
        row.setStyle("-fx-background-color: #F8F8F8; -fx-padding: 12; " +
                "-fx-background-radius: 8;");

        VBox info = new VBox(3);
        info.getChildren().addAll(
                styledLabel(doctorName, "#111111", "13px"),
                styledLabel(date, "#666666", "12px")
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String dotColor = "Pending".equals(status) ? "#E67E22" : "#27AE60";
        Label statusDot = new Label("● " + status);
        statusDot.setStyle("-fx-text-fill: " + dotColor + "; -fx-font-size: 12px;");

        row.getChildren().addAll(info, spacer, statusDot);
        return row;
    }

    private VBox buildDetailCard(String[] row) {
        // row: [0]=id [1]=doctor [2]=diagnosis [3]=recommendation [4]=date
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