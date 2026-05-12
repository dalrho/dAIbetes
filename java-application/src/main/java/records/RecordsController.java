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
import register.sceneLoader;

import java.time.LocalDate;

public class RecordsController {

    // ================= TABLE =================
    @FXML private TableView<Record> recordsTable;

    @FXML private TableColumn<Record, String> patientIdColumn;
    @FXML private TableColumn<Record, String> patientNameColumn;
    @FXML private TableColumn<Record, String> scanDateColumn;
    @FXML private TableColumn<Record, String> scanTypeColumn;
    @FXML private TableColumn<Record, String> statusColumn;
    @FXML private TableColumn<Record, String> diagnosisColumn;

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

    // ================= INIT =================
    @FXML
    public void initialize() {

        // table bindings (simple + safe)
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        scanDateColumn.setCellValueFactory(new PropertyValueFactory<>("scanDate"));
        scanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("scanType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));

        // sample data
        recordList.add(new Record("P001", "Juan Dela Cruz", "2026-05-08", "X-Ray", "Pending", "N/A"));
        recordList.add(new Record("P002", "Maria Santos", "2026-05-07", "MRI", "Completed", "Normal"));
        recordList.add(new Record("P003", "Pedro Reyes", "2026-05-06", "CT Scan", "Pending", "N/A"));

        recordsTable.setItems(recordList);

        // status filter setup
        statusFilter.setItems(FXCollections.observableArrayList("All", "Pending", "Completed"));
        statusFilter.setValue("All");

        updateStatus("System Ready");
    }

    // ================= BUTTON ACTIONS =================

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

    // ================= OPTIONAL SEARCH (BASIC) =================
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

        Scene scene = sceneLoader.load(
                "doctorDashboard",
                "doctor-dashboard.fxml",
                null
        );

        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            // 🔥 IMPORTANT: preserve dashboard size
            stage.setWidth(900);
            stage.setHeight(600);
            stage.setResizable(false);
            stage.show();
        }
    }
    // ================= STATUS BAR =================
    private void updateStatus(String message) {
        statusBarLabel.setText(message);
        lastUpdatedLabel.setText(LocalDate.now().toString());
    }
}