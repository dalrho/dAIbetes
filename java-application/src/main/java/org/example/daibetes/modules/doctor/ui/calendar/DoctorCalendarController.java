package org.example.daibetes.modules.doctor.ui.calendar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.CalendarDAO;
import org.example.daibetes.shared.models.Appointment;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.ui.SceneLoader;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DoctorCalendarController implements Initializable {

    @FXML private GridPane calendarGrid;
    @FXML private GridPane dayHeaderGrid;
    @FXML private Label monthYearLabel;
    @FXML private Label doctorNameLabel;
    @FXML private Label doctorIdLabel;
    
    @FXML private VBox bookedList;
    @FXML private VBox pendingList;
    @FXML private StackPane unreadBadge;
    @FXML private Label unreadCountLabel;

    @FXML private StackPane overlay;
    @FXML private VBox appointmentPopup;
    @FXML private Label popupPatientLabel;
    @FXML private Label popupIdLabel;
    @FXML private Label popupDateLabel;
    @FXML private Label popupTimeLabel;
    @FXML private Label popupStatusLabel;
    @FXML private HBox actionButtons;
    @FXML private Button closePopupBtn;

    private YearMonth currentMonth;
    private Appointment selectedAppointment;
    private int doctorId;
    private final CalendarDAO dao = new CalendarDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();
        
        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser instanceof Doctor doc) {
            doctorId = doc.getDId();
            doctorNameLabel.setText("Doc. " + doc.getFirstname() + " " + doc.getLastname());
            doctorIdLabel.setText("ID: " + doctorId);
        }

        loadAppointmentsFromDB();
        setupDayHeaders();
        refreshUI();
    }

    private void loadAppointmentsFromDB() {
        if (doctorId > 0) {
            List<Appointment> appointments = dao.getAppointmentsByDoctor(doctorId);
            AppContext.getInstance().setAppointments(appointments);
        }
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
        refreshLists();
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
            if (app.isRejected()) continue; // Skip rejected ones on the calendar grid

            VBox block = new VBox();
            block.getStyleClass().add(app.isAccepted() ? "appointment-block" : "appointment-block-pending");
            block.setMaxWidth(Double.MAX_VALUE);
            
            Label nameLabel = new Label(app.getPatientName());
            nameLabel.getStyleClass().add(app.isAccepted() ? "appointment-block-label" : "appointment-block-pending-label");
            nameLabel.setMaxWidth(Double.MAX_VALUE);
            nameLabel.setEllipsisString("...");
            
            block.getChildren().add(nameLabel);
            block.setOnMouseClicked(e -> showAppointmentPopup(app));
            
            cell.getChildren().add(block);
        }

        return cell;
    }

    private void showAppointmentPopup(Appointment app) {
        this.selectedAppointment = app;
        popupPatientLabel.setText("Patient Name: " + app.getPatientName());
        popupIdLabel.setText("Patient ID: " + app.getPatientId());
        popupDateLabel.setText("Date: " + app.getDate().toString());
        popupTimeLabel.setText("Time: " + app.getTime());
        popupStatusLabel.setText("Status: " + (app.isAccepted() ? "Confirmed" : "Awaiting Approval"));

        if (app.isAccepted()) {
            actionButtons.setVisible(false);
            actionButtons.setManaged(false);

            closePopupBtn.setVisible(true);
            closePopupBtn.setManaged(true);
        } else {
            actionButtons.setVisible(true);
            actionButtons.setManaged(true);

            closePopupBtn.setVisible(false);
            closePopupBtn.setManaged(false);
        }
        
        overlay.setVisible(true);
        appointmentPopup.setVisible(true);
    }

    @FXML
    private void handleAccept() {
        if (selectedAppointment != null) {
            boolean success = dao.updateAppointmentStatus(selectedAppointment.getRequestId(), true);
            if (success) {
                loadAppointmentsFromDB();
                closeOverlay();
                refreshUI();
            }
        }
    }

    @FXML
    private void handleReschedule() {
        if (selectedAppointment != null) {
            boolean success = dao.updateAppointmentStatus(selectedAppointment.getRequestId(), false);
            if (success) {
                loadAppointmentsFromDB();
                closeOverlay();
                refreshUI();
            }
        }
    }

    @FXML
    private void closeOverlay() {
        overlay.setVisible(false);
        appointmentPopup.setVisible(false);
        selectedAppointment = null;
    }

    private void refreshLists() {
        bookedList.getChildren().clear();
        pendingList.getChildren().clear();

        List<Appointment> allAppointments = AppContext.getInstance().getAppointments();
        LocalDate today = LocalDate.now();

        List<Appointment> approvedToday = allAppointments.stream()
                .filter(a -> a.isAccepted() && a.getDate().equals(today))
                .collect(Collectors.toList());

        for (Appointment app : approvedToday) {
            Label label = new Label(app.getTime() + " - " + app.getPatientName());
            label.getStyleClass().add("schedule-item");
            label.setOnMouseClicked(e -> showAppointmentPopup(app));
            bookedList.getChildren().add(label);
        }

        if (approvedToday.isEmpty()) {
            bookedList.getChildren().add(new Label("No appointments today."));
        }

        List<Appointment> pending = allAppointments.stream()
                .filter(Appointment::isPending)
                .collect(Collectors.toList());

        for (Appointment app : pending) {
            Label label = new Label(app.getDate().toString() + " - " + app.getPatientName());
            label.getStyleClass().add("schedule-item-pending");
            label.setOnMouseClicked(e -> showAppointmentPopup(app));
            pendingList.getChildren().add(label);
        }

        int pendingCount = pending.size();
        if (pendingCount > 0) {
            unreadBadge.setVisible(true);
            unreadCountLabel.setText(String.valueOf(pendingCount));
        } else {
            unreadBadge.setVisible(false);
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
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(SceneLoader.load("org/example/daibetes/modules/doctor/dashboard", "doctor-dashboard.fxml", null));
    }
}