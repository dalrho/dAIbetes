package records;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.MyPatientsDAO;
import org.example.daibetes.modules.doctor.ui.patients.MyPatientReport;
import register.sceneLoader;

import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

public class RecordsController {

    // ================= TABLE =================
    @FXML private TableView<Record> recordsTable;

    @FXML private TableColumn<Record, String> patientIdColumn;
    @FXML private TableColumn<Record, String> patientNameColumn;
    @FXML private TableColumn<Record, String> scanDateColumn;
    @FXML private TableColumn<Record, String> scanTypeColumn;
    @FXML private TableColumn<Record, String> statusColumn;
    @FXML private TableColumn<Record, String> diagnosisColumn;

    // Updated to handle the button column
    @FXML private TableColumn<Record, Void> actionsColumn;

    // ================= CONTROLS =================
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    // ================= LABELS =================
    @FXML private Label statusBarLabel;
    @FXML private Label lastUpdatedLabel;

    // ================= DATA =================
    private final ObservableList<Record> recordList = FXCollections.observableArrayList();

    int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
    int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();
    String patientName = AppContext.getInstance().getSelectedRecordsPatientName();
    // ================= INIT =================
    @FXML
    public void initialize() {
        int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
        int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();
        String patientName = AppContext.getInstance().getSelectedRecordsPatientName();

        System.out.println("Loading records for patient ID: " + patientId);
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Patient Name: " + patientName);

        loadReports(patientId, doctorId);
    }

    private void loadReports(int patientId, int doctorId) {
        MyPatientsDAO dao = new MyPatientsDAO();

        List<MyPatientReport> reports = dao.getReportsByPatientAndDoctor(patientId, doctorId);

        for (MyPatientReport report : reports) {
            System.out.println("Report ID: " + report.getReportId());
            System.out.println("Date: " + report.getLastReported());
            System.out.println("Criticality: " + report.getCriticalityLevel());
        }

        // Later, replace the println with UI cards/table/list display.
    }

    // ================= BUTTON ACTIONS =================

    private void handleViewDetails() {
        // Logic to open the diagnosis report
        Stage stage = (Stage) recordsTable.getScene().getWindow();

        Scene scene = sceneLoader.load(
                "editGenerateReport",
                "edit-generate-report.fxml",
                null
        );

        if (scene == null) {
            System.out.println("Failed to load editGenerateReport scene");
            return;
        }

        stage.setScene(scene);
        stage.setTitle("dAIbetes — Generate Report");
        stage.show();
    }

    @FXML
    private void handleRefresh() {
        recordsTable.refresh();
        updateStatus("Table refreshed");
    }

    @FXML
    private void handleAddRecord() {
        Record newRecord = new Record(
                "P" + (recordList.size() + 1),
                "New Patient",
                LocalDate.now().toString(),
                "Scan",
                "Pending",
                "N/A"
        );

        recordList.add(newRecord);
        updateStatus("New record added");
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        statusFilter.setValue("All");
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        recordsTable.setItems(recordList);
        updateStatus("Filters cleared");
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            recordsTable.setItems(recordList);
            return;
        }

        ObservableList<Record> filtered = FXCollections.observableArrayList();
        for (Record r : recordList) {
            if (r.getPatientId().toLowerCase().contains(keyword) ||
                    r.getPatientName().toLowerCase().contains(keyword)) {
                filtered.add(r);
            }
        }
        recordsTable.setItems(filtered);
        updateStatus("Searching: " + keyword);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Scene scene = sceneLoader.load("doctorDashboard", "doctor-dashboard.fxml", null);
        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.show();
        }
    }

    private void updateStatus(String message) {
        statusBarLabel.setText(message);
        lastUpdatedLabel.setText(LocalDate.now().toString());
    }
}