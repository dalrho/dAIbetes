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
import org.example.daibetes.core.database.CalendarDAO;
import org.example.daibetes.core.domain.Appointment;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;
import register.sceneLoader;

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

    // ── Request popup ─────────────────────────────────────────────────────────
    @FXML private ComboBox<String> doctorComboBox;
    @FXML private DatePicker       requestDatePicker;
    @FXML private TextField        timeField;

    // ── State ─────────────────────────────────────────────────────────────────
    private YearMonth      currentMonth;
    private int            patientId;
    private final CalendarDAO dao = new CalendarDAO();

    // Maps ComboBox display string → d_id for DB insert
    private final List<String[]> doctorRows = new ArrayList<>();

    // =========================================================================
    // Initialize
    // =========================================================================

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();

        // Resolve patient from session
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

    // =========================================================================
    // DB loaders
    // =========================================================================

    private void loadDoctorsIntoComboBox() {
        doctorRows.clear();
        doctorRows.addAll(dao.getAllDoctors());

        List<String> displayNames = doctorRows.stream()
                .map(row -> row[1])
                .collect(Collectors.toList());

        doctorComboBox.setItems(FXCollections.observableArrayList(displayNames));
    }

    private void loadAppointmentsFromDB() {
        List<Appointment> appointments = dao.getAppointmentsByPatient(patientId);
        AppContext.getInstance().setAppointments(appointments);
    }

    // =========================================================================
    // Calendar rendering
    // =========================================================================

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

        String monthName = currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                .toUpperCase();
        monthYearLabel.setText(monthName + " " + currentMonth.getYear());

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int offset      = firstOfMonth.getDayOfWeek().getValue() - 1;
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

    private VBox buildDayCell(int day, LocalDate date,
                              List<Appointment> appointments) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("calendar-cell");
        cell.setAlignment(Pos.TOP_LEFT);

        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setStyle(date.equals(LocalDate.now())
                ? "-fx-text-fill: #7B2528; -fx-font-weight: bold; -fx-font-size: 11px;"
                : "-fx-text-fill: #888; -fx-font-size: 11px;");
        cell.getChildren().add(dayLabel);

        List<Appointment> dayApps = appointments.stream()
                .filter(a -> a.getDate().equals(date))
                .collect(Collectors.toList());

        for (Appointment app : dayApps) {
            VBox block = new VBox();
            block.setMaxWidth(Double.MAX_VALUE);
            block.setStyle("-fx-background-color: " + app.getStatusColor() +
                    "; -fx-background-radius: 4; -fx-padding: 2 4;");

            Label nameLabel = new Label(app.getDoctorName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            block.getChildren().add(nameLabel);
            block.setOnMouseClicked(e -> handleAppointmentClick(app));
            cell.getChildren().add(block);
        }

        return cell;
    }

    // =========================================================================
    // Appointment list sidebar
    // =========================================================================

    private void refreshAppointmentList() {
        appointmentList.getChildren().clear();
        LocalDate today = LocalDate.now();

        List<Appointment> future = AppContext.getInstance().getAppointments()
                .stream()
                .filter(a -> !a.getDate().isBefore(today))
                .collect(Collectors.toList());

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
        item.setStyle("-fx-background-color: #F8F8F8; -fx-padding: 10; " +
                "-fx-background-radius: 8; -fx-cursor: hand;");

        Label doctorLabel = new Label(app.getDoctorName());
        doctorLabel.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        Label dateLabel = new Label(app.getDate().toString() + "  " + app.getTime());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");

        Label statusLabel = new Label("● " + app.getStatusLabel().toUpperCase());
        statusLabel.setStyle("-fx-text-fill: " + app.getStatusColor() +
                "; -fx-font-size: 11px; -fx-font-weight: bold;");

        item.getChildren().addAll(doctorLabel, dateLabel, statusLabel);
        return item;
    }

    // =========================================================================
    // Appointment detail popup
    // =========================================================================

    private void handleAppointmentClick(Appointment app) {
        popupDoctorLabel.setText("Doctor: " + app.getDoctorName());
        popupDateLabel.setText("Date: " + app.getDate().toString());
        popupTimeLabel.setText("Time: " + app.getTime());
        popupStatusLabel.setText("Status: " + app.getStatusLabel());
        popupStatusLabel.setStyle("-fx-text-fill: " + app.getStatusColor() +
                "; -fx-font-weight: bold;");

        overlay.setVisible(true);
        appointmentPopup.setVisible(true);
        requestPopup.setVisible(false);
    }

    // =========================================================================
    // Request appointment popup
    // =========================================================================

    @FXML
    private void showRequestPopup() {
        requestDatePicker.setValue(null);
        timeField.clear();
        doctorComboBox.getSelectionModel().clearSelection();

        overlay.setVisible(true);
        appointmentPopup.setVisible(false);
        requestPopup.setVisible(true);
    }

    @FXML
    private void handleRequestSubmit() {
        String selectedDoctor = doctorComboBox.getValue();
        LocalDate date        = requestDatePicker.getValue();
        String time           = timeField.getText().trim();

        if (selectedDoctor == null || date == null || time.isEmpty()) {
            showAlert("Please fill in all fields.");
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            showAlert("Please select a future date.");
            return;
        }

        // Resolve d_id from selected display name
        int selectedDoctorId = doctorRows.stream()
                .filter(row -> row[1].equals(selectedDoctor))
                .findFirst()
                .map(row -> Integer.parseInt(row[0]))
                .orElse(-1);

        if (selectedDoctorId == -1) {
            showAlert("Could not resolve selected doctor. Please try again.");
            return;
        }

        // Build requested_on datetime: "YYYY-MM-DD HH:MM:SS"
        String requestedOn = buildRequestedOn(date, time);
        if (requestedOn == null) {
            showAlert("Invalid time format. Please use e.g. 10:00 AM or 14:00.");
            return;
        }

        int requestId = dao.insertAppointmentRequest(
                patientId, selectedDoctorId, requestedOn);

        if (requestId != -1) {
            loadAppointmentsFromDB(); // reload from DB — no manual Appointment construction
            refreshUI();
            closeOverlay();
        } else {
            showAlert("Failed to submit request. Please try again.");
        }
    }

    // =========================================================================
    // Navigation handlers
    // =========================================================================

    @FXML
    private void previousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        refreshUI();
    }

    @FXML
    private void nextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        refreshUI();
    }

    @FXML
    private void closeOverlay() {
        overlay.setVisible(false);
        appointmentPopup.setVisible(false);
        requestPopup.setVisible(false);
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(sceneLoader.load(
                "patientsdashboard", "patients-dashboard.fxml", null));
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    /**
     * Parses free-text time input into "HH:MM:SS" suffix for the datetime string.
     * Supports: "10:00 AM", "14:00", "2pm", "2:30pm"
     */
    private String buildRequestedOn(LocalDate date, String timeInput) {
        try {
            String normalized = timeInput.trim().toUpperCase()
                    .replaceAll("\\s+", " ");

            int hour, minute;

            if (normalized.contains("AM") || normalized.contains("PM")) {
                boolean pm = normalized.contains("PM");
                String numPart = normalized.replace("AM", "").replace("PM", "").trim();
                String[] parts = numPart.split(":");
                hour   = Integer.parseInt(parts[0].trim());
                minute = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
                if (pm && hour != 12) hour += 12;
                if (!pm && hour == 12) hour = 0;
            } else {
                String[] parts = normalized.split(":");
                hour   = Integer.parseInt(parts[0].trim());
                minute = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
            }

            return String.format("%s %02d:%02d:00", date, hour, minute);

        } catch (Exception e) {
            return null;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleDiagnosis(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load(
                        "patientsdashboard",
                        "patients-dashboard.fxml",
                        null
                )
        );

        stage.setTitle("dAIbetes — Patient Dashboard");
        stage.show();
    }
}