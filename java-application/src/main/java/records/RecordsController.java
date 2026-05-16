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
import register.sceneLoader;

import java.time.LocalDate;
import java.util.List;

public class RecordsController {

    @FXML private TableView<Record> recordsTable;

    @FXML private TableColumn<Record, String> patientIdColumn;
    @FXML private TableColumn<Record, String> patientNameColumn;
    @FXML private TableColumn<Record, String> scanDateColumn;
    @FXML private TableColumn<Record, String> scanTypeColumn;
    @FXML private TableColumn<Record, String> statusColumn;
    @FXML private TableColumn<Record, String> diagnosisColumn;

    @FXML private TableColumn<Record, Void> actionsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private Label statusBarLabel;
    @FXML private Label lastUpdatedLabel;

    private final ObservableList<Record> recordList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();

        int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
        int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();
        String patientName = AppContext.getInstance().getSelectedRecordsPatientName();

        System.out.println("Loading records for patient ID: " + patientId);
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Patient Name: " + patientName);

        loadReports(patientId, doctorId);
    }

    private void setupTableColumns() {
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        scanDateColumn.setCellValueFactory(new PropertyValueFactory<>("scanDate"));

        // This column is labeled Follow-up in your FXML
        scanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("followUp"));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // This column is labeled Criticality Level in your FXML
        diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("criticalityLevel"));
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("All", "YES", "NO"));
        statusFilter.setValue("All");
    }

    private void loadReports(int patientId, int doctorId) {
        if (patientId == 0 || doctorId == 0) {
            updateStatus("No selected patient or doctor found.");
            return;
        }

        MyPatientsDAO dao = new MyPatientsDAO();

        List<Record> records = dao.getRecordTableByPatientAndDoctor(patientId, doctorId);

        recordList.setAll(records);
        recordsTable.setItems(recordList);

        updateStatus(records.size() + " record(s) loaded");
    }

    @FXML
    private void handleRefresh() {
        int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
        int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();

        loadReports(patientId, doctorId);
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
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();

        String followUpFilter = statusFilter.getValue();

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        ObservableList<Record> filtered = FXCollections.observableArrayList();

        for (Record record : recordList) {
            boolean matchesKeyword = keyword.isEmpty()
                    || record.getPatientId().toLowerCase().contains(keyword)
                    || record.getPatientName().toLowerCase().contains(keyword);

            boolean matchesFollowUp = followUpFilter == null
                    || followUpFilter.equals("All")
                    || record.getFollowUp().equalsIgnoreCase(followUpFilter);

            boolean matchesDate = true;

            try {
                LocalDate scanDate = LocalDate.parse(record.getScanDate().substring(0, 10));

                if (fromDate != null && scanDate.isBefore(fromDate)) {
                    matchesDate = false;
                }

                if (toDate != null && scanDate.isAfter(toDate)) {
                    matchesDate = false;
                }

            } catch (Exception ignored) {
                // If date cannot be parsed, do not block display
            }

            if (matchesKeyword && matchesFollowUp && matchesDate) {
                filtered.add(record);
            }
        }

        recordsTable.setItems(filtered);
        updateStatus(filtered.size() + " matching record(s)");
    }

    @FXML
    private void handleAddRecord() {
        updateStatus("Add record is not available from this screen.");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Scene scene = sceneLoader.load(
                "myPatients",
                "my-patients-view.fxml",
                "/styles/myPatient.css"
        );

        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle("My Patients");
            stage.show();
        }
    }

    private void updateStatus(String message) {
        statusBarLabel.setText(message);
        lastUpdatedLabel.setText(LocalDate.now().toString());
    }
}