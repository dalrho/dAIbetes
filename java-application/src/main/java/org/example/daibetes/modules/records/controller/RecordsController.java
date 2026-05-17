    package org.example.daibetes.modules.records.controller;
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
    import org.example.daibetes.core.database.DoctorDashboardDAO;
    import org.example.daibetes.core.database.MyPatientsDAO;
    import org.example.daibetes.shared.ui.PopupManager;
    import org.example.daibetes.shared.ui.SceneLoader;
    import org.example.daibetes.modules.records.model.Record;
    import java.time.LocalDate;
    import java.util.List;

    import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

    public class RecordsController {

        @FXML private TableView<Record> recordsTable;

        @FXML private TableColumn<Record, String> patientIdColumn;
        @FXML private TableColumn<Record, String> patientNameColumn;
        @FXML private TableColumn<Record, String> scanDateColumn;
        @FXML private TableColumn<Record, String> scanTypeColumn;
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
            setupActionsColumn();
            setupFilters();

            searchField.setOnAction(e -> handleSearch());

            int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
            int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();

            loadReports(patientId, doctorId);
        }

        private void setupTableColumns() {
            patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
            patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
            scanDateColumn.setCellValueFactory(new PropertyValueFactory<>("scanDate"));

            // This column is labeled Follow-up in your FXML
            scanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("followUp"));



            // This column is labeled Criticality Level in your FXML
            diagnosisColumn.setCellValueFactory(new PropertyValueFactory<>("criticalityLevel"));
        }

        private void setupFilters() {
            statusFilter.setItems(FXCollections.observableArrayList("All", "YES", "NO"));
            statusFilter.setValue("All");

            statusFilter.setOnAction(e -> handleSearch());
            fromDatePicker.setOnAction(e -> handleSearch());
            toDatePicker.setOnAction(e -> handleSearch());
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

            // Clear filters so refreshed database records are shown fully
            searchField.clear();
            statusFilter.setValue("All");
            fromDatePicker.setValue(null);
            toDatePicker.setValue(null);

            // Re-read from database
            loadReports(patientId, doctorId);

            updateStatus("Records refreshed from database.");
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

            if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
                showAlert("Invalid Date Range", "From date cannot be after To date.");
                return;
            }

            ObservableList<Record> filtered = FXCollections.observableArrayList();

            for (Record record : recordList) {

                boolean matchesKeyword = keyword.isEmpty()
                        || record.getCriticalityLevel().toLowerCase().contains(keyword);

                boolean matchesFollowUpStatus = followUpFilter == null
                        || followUpFilter.equalsIgnoreCase("All")
                        || record.getFollowUp().equalsIgnoreCase(followUpFilter);

                boolean matchesDateRange = true;

                try {
                    LocalDate scanDate = LocalDate.parse(record.getScanDate().substring(0, 10));

                    if (fromDate != null && scanDate.isBefore(fromDate)) {
                        matchesDateRange = false;
                    }

                    if (toDate != null && scanDate.isAfter(toDate)) {
                        matchesDateRange = false;
                    }

                } catch (Exception e) {
                    matchesDateRange = false;
                }

                if (matchesKeyword && matchesFollowUpStatus && matchesDateRange) {
                    filtered.add(record);
                }
            }

            recordsTable.setItems(filtered);
            updateStatus(filtered.size() + " matching record(s)");
        }

        @FXML
        private void handleAddRecord() {
            int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
            int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();
            String patientName = AppContext.getInstance().getSelectedRecordsPatientName();

            if (patientId == 0 || doctorId == 0 || patientName == null || patientName.isBlank()) {
                showAlert("Missing Data", "No selected patient or doctor found.");
                return;
            }

            /*
             * Store selected patient and doctor so the next screens can use them.
             */
            AppContext.getInstance().setSelectedRecordsPatientId(patientId);
            AppContext.getInstance().setSelectedRecordsDoctorId(doctorId);
            AppContext.getInstance().setSelectedRecordsPatientName(patientName);

            /*
             * Optional: if other controllers use selectedPatientName instead,
             * store it there too.
             */
            AppContext.getInstance().setSelectedPatientName(patientName);

            /*
             * Clear old report selection because this is a NEW diagnosis.
             */
            AppContext.getInstance().setSelectedReportId(0);

            PopupManager.open(
                    "org/example/daibetes/modules/doctor/ui/popup",
                    "popdiagnosis-screen.fxml",
                    "/org/example/daibetes/styles/new-diagnosis.css",
                    "New Diagnosis"
            );

            updateStatus("Opening new diagnosis for " + patientName);
        }

        @FXML
        private void handleBack(ActionEvent event) {
            Scene scene = SceneLoader.load(
                    "org/example/daibetes/modules/doctor/ui/patients",
                    "my-patients-view.fxml",
                    "/styles/myPatient.css"
            );

            if (scene == null) {
                showAlert("Navigation Error", "Could not return to My Patients screen.");
                return;
            }

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle("My Patients");
            stage.show();
        }

        private void updateStatus(String message) {
            statusBarLabel.setText(message);
            lastUpdatedLabel.setText(LocalDate.now().toString());
        }
        private void setupActionsColumn() {
            actionsColumn.setCellFactory(column -> new TableCell<>() {
                private final Button viewButton = new Button("View");

                {
                    viewButton.setStyle(
                            "-fx-background-color: #0066CC;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 11;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: 6;" +
                                    "-fx-cursor: hand;"
                    );

                    viewButton.setOnAction(event -> {
                        Record record = getTableView().getItems().get(getIndex());
                        openDetailedReport(record);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(viewButton);
                    }
                }
            });
        }
        private void openDetailedReport(Record record) {
            AppContext.getInstance().setSelectedReportId(record.getReportId());

            int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();

            if (doctorId != 0) {
                DoctorDashboardDAO dashboardDAO = new DoctorDashboardDAO();
                dashboardDAO.logOpenedReport(doctorId, record.getReportId());
            }

            Stage stage = (Stage) recordsTable.getScene().getWindow();

            Scene scene = SceneLoader.load(
                    "org/example/daibetes/modules/doctor/ui/review",
                    "doctorViewDiagnosis.fxml",
                    null
            );

            if (scene == null) {
                updateStatus("Could not open detailed report.");
                return;
            }

            stage.setScene(scene);
            stage.setTitle("Detailed Report");
            stage.show();
        }
    }