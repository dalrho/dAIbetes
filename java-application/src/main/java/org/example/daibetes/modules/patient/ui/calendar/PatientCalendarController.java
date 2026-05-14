package org.example.daibetes.modules.patient.ui.calendar;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.domain.Appointment;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;
import register.sceneLoader;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PatientCalendarController implements Initializable {

    @FXML private GridPane calendarGrid;
    @FXML private GridPane dayHeaderGrid;
    @FXML private Label monthYearLabel;
    @FXML private ImageView profileImage;
    @FXML private Label patientNameLabel;
    @FXML private Label patientIdLabel;
    @FXML private VBox appointmentList;

    @FXML private StackPane overlay;
    @FXML private VBox appointmentPopup;
    @FXML private VBox requestPopup;

    @FXML private Label popupDoctorLabel;
    @FXML private Label popupDateLabel;
    @FXML private Label popupTimeLabel;
    @FXML private Label popupStatusLabel;

    @FXML private ComboBox<String> doctorComboBox;
    @FXML private DatePicker requestDatePicker;
    @FXML private TextField timeField;

    private YearMonth currentMonth;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();
        
        // Initialize Profile
        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser instanceof Patient) {
            Patient p = (Patient) currentUser;
            patientNameLabel.setText(p.getFirstname() + " " + p.getLastname());
            patientIdLabel.setText("ID: " + p.getPId());
        }

        // Initialize Doctor ComboBox
        doctorComboBox.setItems(FXCollections.observableArrayList("Dr. Cruz", "Dr. Santos", "Dr. Reyes", "Dr. Garcia"));

        setupDayHeaders();
        refreshUI();
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

        String monthName = currentMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                .toUpperCase();
        monthYearLabel.setText(monthName + " " + currentMonth.getYear());

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int offset = firstOfMonth.getDayOfWeek().getValue() - 1;

        int daysInMonth = currentMonth.lengthOfMonth();
        int row = 0;
        int col = offset;

        List<Appointment> allAppointments = AppContext.getInstance().getAppointments();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            VBox cell = buildDayCell(day, date, allAppointments);

            GridPane.setHgrow(cell, Priority.ALWAYS);
            GridPane.setVgrow(cell, Priority.ALWAYS);
            calendarGrid.add(cell, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private VBox buildDayCell(int day, LocalDate date, List<Appointment> appointments) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("calendar-cell");
        cell.setAlignment(Pos.TOP_LEFT);

        Label dayLabel = new Label(String.valueOf(day));
        dayLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");
        cell.getChildren().add(dayLabel);

        List<Appointment> dayApps = appointments.stream()
                .filter(a -> a.getDate().equals(date))
                .collect(Collectors.toList());

        for (Appointment app : dayApps) {
            VBox block = new VBox();
            block.getStyleClass().add(app.isAccepted() ? "appointment-block" : "appointment-block-pending");
            block.setMaxWidth(Double.MAX_VALUE);
            
            Label nameLabel = new Label(app.getDoctorName());
            nameLabel.getStyleClass().add(app.isAccepted() ? "appointment-block-label" : "appointment-block-pending-label");
            nameLabel.setEllipsisString("...");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            
            block.getChildren().add(nameLabel);
            block.setOnMouseClicked(e -> handleAppointmentClick(app));
            
            cell.getChildren().add(block);
        }

        return cell;
    }

    private void handleAppointmentClick(Appointment app) {
        popupDoctorLabel.setText("Doctor: " + app.getDoctorName());
        popupDateLabel.setText("Date: " + app.getDate().toString());
        popupTimeLabel.setText("Time: " + app.getTime());
        popupStatusLabel.setText("Status: " + (app.isAccepted() ? "Confirmed" : "Pending Approval"));
        
        overlay.setVisible(true);
        appointmentPopup.setVisible(true);
        requestPopup.setVisible(false);
    }

    private void refreshAppointmentList() {
        appointmentList.getChildren().clear();
        LocalDate today = LocalDate.now();
        List<Appointment> appointments = AppContext.getInstance().getAppointments();
        
        // Only show future appointments in the sidebar list
        List<Appointment> futureApps = appointments.stream()
                .filter(a -> !a.getDate().isBefore(today))
                .collect(Collectors.toList());

        for (Appointment app : futureApps) {
            String status = app.isAccepted() ? "[ACCEPTED]" : "[PENDING]";
            Label label = new Label(app.getDate().toString() + " - " + app.getTime() + "\n" + status);
            label.getStyleClass().add(app.isAccepted() ? "schedule-item" : "schedule-item-pending");
            label.setOnMouseClicked(e -> handleAppointmentClick(app));
            appointmentList.getChildren().add(label);
        }
        
        if (futureApps.isEmpty()) {
            appointmentList.getChildren().add(new Label("No upcoming appointments."));
        }
    }

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
    private void showRequestPopup() {
        overlay.setVisible(true);
        appointmentPopup.setVisible(false);
        requestPopup.setVisible(true);
    }

    @FXML
    private void closeOverlay() {
        overlay.setVisible(false);
        appointmentPopup.setVisible(false);
        requestPopup.setVisible(false);
    }

    @FXML
    private void handleRequestSubmit() {
        String doc = doctorComboBox.getValue();
        LocalDate date = requestDatePicker.getValue();
        String time = timeField.getText();

        if (doc == null || date == null || time == null || time.isEmpty()) {
            System.out.println("Please fill all fields.");
            return;
        }

        User currentUser = AppContext.getInstance().getCurrentUser();
        int patientId = (currentUser instanceof Patient) ? ((Patient) currentUser).getPId() : 0;
        String patientName = (currentUser != null) ? currentUser.getFirstname() + " " + currentUser.getLastname() : "Unknown";

        // Create new appointment (id is auto-generated by list size for now)
        int newId = AppContext.getInstance().getAppointments().size() + 1;
        Appointment newApp = new Appointment(newId, patientId, 1, patientName, doc, date, time, false);
        
        AppContext.getInstance().addAppointment(newApp);
        
        closeOverlay();
        refreshUI();
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(sceneLoader.load("patientsdashboard", "patients-dashboard.fxml", null));
    }
}
