package org.example.daibetes.modules.patient.ui.calendar;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ICalendarDAO;
import org.example.daibetes.shared.models.Appointment;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.shared.utils.ServiceRegistry;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PatientCalendarController implements Initializable {

    // ── Calendar ─────────────────────────────────────────────────────────────
    @FXML private GridPane  calendarGrid;
    @FXML private GridPane  dayHeaderGrid;
    @FXML private Label     monthYearLabel;

    // ── Sidebar profile ───────────────────────────────────────────────────────
    @FXML private ImageView profileImage;
    @FXML private Label     patientNameLabel;
    @FXML private Label     patientIdLabel;

    // ── Appointment list ──────────────────────────────────────────────────────
    @FXML private VBox      appointmentList;

    // ── Overlay / popups ──────────────────────────────────────────────────────
    @FXML private StackPane overlay;
    @FXML private VBox      appointmentPopup;
    @FXML private VBox      requestPopup;

    // ── Appointment detail popup ──────────────────────────────────────────────
    @FXML private Label     popupDoctorLabel;
    @FXML private Label     popupDateLabel;
    @FXML private Label     popupTimeLabel;
    @FXML private Label     popupStatusLabel;
    @FXML private Button    removeBtn; // Added for removal feature

    // ── Request popup (UPDATED TO USE CLOCK COMBOS) ───────────────────────────
    @FXML private ComboBox<String> doctorComboBox;
    @FXML private DatePicker       requestDatePicker;
    @FXML private ComboBox<String> hourCombo;
    @FXML private ComboBox<String> minuteCombo;
    @FXML private ComboBox<String> amPmCombo;

    // ── State ─────────────────────────────────────────────────────────────────
    private YearMonth      currentMonth;
    private int            patientId;
    private final ICalendarDAO dao = ServiceRegistry.getInstance().get(ICalendarDAO.class);
    private Appointment selectedAppointmentForDetail; // Tracks current selected app

    private final List<String[]> doctorRows = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();

        // Setup Clock Dropdowns
        hourCombo.setItems(FXCollections.observableArrayList("01","02","03","04","05","06","07","08","09","10","11","12"));
        minuteCombo.setItems(FXCollections.observableArrayList("00","15","30","45"));
        amPmCombo.setItems(FXCollections.observableArrayList("AM","PM"));

        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser instanceof Patient p) {
            patientId = p.getPId();
            patientNameLabel.setText(p.getFirstname() + " " + p.getLastname());
            patientIdLabel.setText("ID: " + patientId);
        }

        loadDoctorsIntoComboBox();
        loadAppointmentsFromDB();
        setupDayHeaders();
        refreshUI();
    }

    private void loadDoctorsIntoComboBox() {
        doctorRows.clear();
        doctorRows.addAll(dao.getAllDoctors());
        if (doctorRows.isEmpty()) {
            doctorComboBox.setPromptText("No doctors available");
            return;
        }
        List<String> displayNames = doctorRows.stream().map(row -> row[1]).collect(Collectors.toList());
        doctorComboBox.setItems(FXCollections.observableArrayList(displayNames));
    }

    private void loadAppointmentsFromDB() {
        List<Appointment> appointments = dao.getAppointmentsByPatient(patientId);
        AppContext.getInstance().setAppointments(appointments);
    }

    private void setupDayHeaders() {
        String[] days = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
        for (int i = 0; i < 7; i++) {
            Label label = new Label(days[i]);
            label.getStyleClass().add("day-name-label");
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            dayHeaderGrid.add(label, i, 0);
        }
    }

    private void refreshUI() {
        drawCalendar();
        refreshAppointmentList();
    }

    private void drawCalendar() {
        calendarGrid.getChildren().clear();
        String monthName = currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH).toUpperCase();
        monthYearLabel.setText(monthName + " " + currentMonth.getYear());

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int offset = firstOfMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();
        int row = 0, col = offset;

        List<Appointment> allAppointments = AppContext.getInstance().getAppointments();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            VBox cell = buildDayCell(day, date, allAppointments);
            GridPane.setHgrow(cell, Priority.ALWAYS);
            GridPane.setVgrow(cell, Priority.ALWAYS);
            calendarGrid.add(cell, col, row);
            col++;
            if (col > 6) { col = 0; row++; }
        }
    }

    private VBox buildDayCell(int day, LocalDate date, List<Appointment> appointments) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("calendar-cell");
        cell.setAlignment(Pos.TOP_LEFT);
        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setStyle(date.equals(LocalDate.now()) ? "-fx-text-fill: #7B2528; -fx-font-weight: bold; -fx-font-size: 11px;" : "-fx-text-fill: #888; -fx-font-size: 11px;");
        cell.getChildren().add(dayLabel);

        List<Appointment> dayApps = appointments.stream().filter(a -> a.getDate().equals(date)).collect(Collectors.toList());
        for (Appointment app : dayApps) {
            VBox block = new VBox();
            block.setMaxWidth(Double.MAX_VALUE);
            block.setStyle("-fx-background-color: " + app.getStatusColor() + "; -fx-background-radius: 4; -fx-padding: 2 4;");
            Label nameLabel = new Label(app.getDoctorName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            block.getChildren().add(nameLabel);
            block.setOnMouseClicked(e -> handleAppointmentClick(app));
            cell.getChildren().add(block);
        }
        return cell;
    }

    private void refreshAppointmentList() {
        appointmentList.getChildren().clear();
        LocalDate today = LocalDate.now();
        List<Appointment> future = AppContext.getInstance().getAppointments().stream().filter(a -> !a.getDate().isBefore(today)).collect(Collectors.toList());
        for (Appointment app : future) {
            VBox item = buildAppointmentListItem(app);
            item.setOnMouseClicked(e -> handleAppointmentClick(app));
            appointmentList.getChildren().add(item);
        }
        if (future.isEmpty()) {
            Label empty = new Label("No upcoming appointments.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-size: 13px;");
            appointmentList.getChildren().add(empty);
        }
    }

    private VBox buildAppointmentListItem(Appointment app) {
        VBox item = new VBox(4);
        item.setStyle("-fx-background-color: #F8F8F8; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
        Label doctorLabel = new Label(app.getDoctorName());
        doctorLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");
        Label dateLabel = new Label(app.getDate().toString() + "  " + app.getTime());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
        Label statusLabel = new Label("● " + app.getStatusLabel().toUpperCase());
        statusLabel.setStyle("-fx-text-fill: " + app.getStatusColor() + "; -fx-font-size: 11px; -fx-font-weight: bold;");
        item.getChildren().addAll(doctorLabel, dateLabel, statusLabel);
        return item;
    }

    private void handleAppointmentClick(Appointment app) {
        this.selectedAppointmentForDetail = app; // Save reference
        popupDoctorLabel.setText("Doctor: " + app.getDoctorName());
        popupDateLabel.setText("Date: " + app.getDate().toString());
        popupTimeLabel.setText("Time: " + app.getTime());
        popupStatusLabel.setText("Status: " + app.getStatusLabel());
        popupStatusLabel.setStyle("-fx-text-fill: " + app.getStatusColor() + "; -fx-font-weight: bold;");

        // SHOW REMOVE BUTTON ONLY IF STATUS IS REJECTED
        boolean isRejected = "REJECTED".equalsIgnoreCase(app.getStatusLabel());
        removeBtn.setVisible(isRejected);
        removeBtn.setManaged(isRejected);

        overlay.setVisible(true);
        appointmentPopup.setVisible(true);
        requestPopup.setVisible(false);
    }

    @FXML
    private void handleRemoveAppointment() {
        if (selectedAppointmentForDetail != null) {
            // Call DAO to delete from database (Ensure your DAO has deleteAppointmentRequest or similar)
            dao.deleteAppointmentRequest(selectedAppointmentForDetail.getRequestId());

            loadAppointmentsFromDB(); // Reload data
            refreshUI();              // Update display
            closeOverlay();           // Close the popup
        }
    }

    @FXML
    private void showRequestPopup() {
        requestDatePicker.setValue(null);
        // Reset Time Combos
        hourCombo.getSelectionModel().clearSelection();
        minuteCombo.getSelectionModel().clearSelection();
        amPmCombo.getSelectionModel().select("AM");
        doctorComboBox.getSelectionModel().clearSelection();

        overlay.setVisible(true);
        appointmentPopup.setVisible(false);
        requestPopup.setVisible(true);
    }

    @FXML
    private void handleRequestSubmit() {
        String selectedDoctor = doctorComboBox.getValue();
        LocalDate date = requestDatePicker.getValue();

        // COMBINE CLOCK VALUES INTO A STRING FOR PARSING
        String h = hourCombo.getValue();
        String m = minuteCombo.getValue();
        String ap = amPmCombo.getValue();

        if (selectedDoctor == null || date == null || h == null || m == null) {
            showAlert("Please fill in all fields (including time).");
            return;
        }

        String timeStr = h + ":" + m + " " + ap;

        if (patientId <= 0) {
            showAlert("Session error: No valid patient ID found. Please log in again.");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            showAlert("Please select a future date.");
            return;
        }

        int selectedDoctorId = doctorRows.stream()
                .filter(row -> row[1].equals(selectedDoctor))
                .findFirst()
                .map(row -> Integer.parseInt(row[0]))
                .orElse(-1);

        if (selectedDoctorId == -1) {
            showAlert("Could not resolve selected doctor.");
            return;
        }

        // We use the existing parsing logic which now receives "10:00 AM" format
        String requestedOn = buildRequestedOn(date, timeStr);

        int requestId = dao.insertAppointmentRequest(patientId, selectedDoctorId, requestedOn);

        if (requestId != -1) {
            loadAppointmentsFromDB();
            refreshUI();
            closeOverlay();
        } else {
            showAlert("Failed to submit request.");
        }
    }

    @FXML private void previousMonth() { currentMonth = currentMonth.minusMonths(1); refreshUI(); }
    @FXML private void nextMonth() { currentMonth = currentMonth.plusMonths(1); refreshUI(); }
    @FXML private void closeOverlay() { overlay.setVisible(false); appointmentPopup.setVisible(false); requestPopup.setVisible(false); }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SceneLoader.load("org/example/daibetes/modules/patient/dashboard", "patients-dashboard.fxml", null));
    }

    private String buildRequestedOn(LocalDate date, String timeInput) {
        try {
            String normalized = timeInput.trim().toUpperCase().replaceAll("\\s+", " ");
            int hour, minute;
            if (normalized.contains("AM") || normalized.contains("PM")) {
                boolean pm = normalized.contains("PM");
                String numPart = normalized.replace("AM", "").replace("PM", "").trim();
                String[] parts = numPart.split(":");
                hour = Integer.parseInt(parts[0].trim());
                minute = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
                if (pm && hour != 12) hour += 12;
                if (!pm && hour == 12) hour = 0;
            } else {
                String[] parts = normalized.split(":");
                hour = Integer.parseInt(parts[0].trim());
                minute = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            }
            return String.format("%s %02d:%02d:00", date, hour, minute);
        } catch (Exception e) { return null; }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDiagnosis(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SceneLoader.load("org/example/daibetes/modules/patient/dashboard", "patients-dashboard.fxml", null));
        stage.setTitle("dAIbetes — Patient Dashboard");
        stage.show();
    }
}