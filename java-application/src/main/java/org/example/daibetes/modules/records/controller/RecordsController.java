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
    import javafx.scene.control.TableCell;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;

    import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

    public class RecordsController {

        @FXML private TableView<Record> recordsTable;

        @FXML private TableColumn<Record, String> patientIdColumn;
        @FXML private TableColumn<Record, String> patientNameColumn;
        @FXML private TableColumn<Record, String> scanDateColumn;
        @FXML private TableColumn<Record, String> scanTypeColumn;
        @FXML private TableColumn<Record, String> diagnosisColumn;
        @FXML private Button backBtn;
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

            // 1. Keep the cell value factory so it knows WHICH data to get
            scanDateColumn.setCellValueFactory(new PropertyValueFactory<>("scanDate"));

            // 2. Add a Cell Factory to change HOW that data is displayed
            scanDateColumn.setCellFactory(column -> new TableCell<Record, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.isBlank()) {
                        setText(null);
                    } else {
                        try {
                            // Assuming input is "yyyy-MM-dd HH:mm:ss" from DB
                            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            // Output format: "May 17, 2026"
                            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

                            // Parse the raw string to a DateTime object
                            LocalDateTime dateTime = LocalDateTime.parse(item, inputFormatter);

                            // Set the readable text to the cell
                            setText(dateTime.format(outputFormatter));
                        } catch (Exception e) {
                            // Fallback: If it's not a full date-time, try parsing just the date
                            try {
                                DateTimeFormatter dateOnlyInput = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate date = LocalDate.parse(item.substring(0, 10), dateOnlyInput);
                                setText(date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
                            } catch (Exception ex) {
                                setText(item); // If all parsing fails, show the original string
                            }
                        }
                    }
                }
            });

            scanTypeColumn.setCellValueFactory(new PropertyValueFactory<>("followUp"));
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


        //fix this
        @FXML
        private void handleDeleteRecord(ActionEvent event) {
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
            AppContext.getInstance().setShowDiagnosisBackButton(true);

            SceneLoader.switchScene(
                    (Node) event.getSource(), // or any UI node in THIS controller
                    "org/example/daibetes/modules/doctor/ui/popup",
                    "pop2.fxml",
                    "Add Diagnosis",
                    "/org/example/daibetes/styles/new-diagnosis.css"
            );
            updateStatus("Opening new diagnosis for " + patientName);
        }

        @FXML
        private void handleBack(ActionEvent event) {
            SceneLoader.switchScene(
                    (Node) event.getSource(),
                    "org/example/daibetes/modules/doctor/ui/patients",
                    "my-patients-view.fxml",
                    "My Patients",
                    "/org/example/daibetes/styles/my-patient.css"
            );
        }

        private void updateStatus(String message) {
            statusBarLabel.setText(message);

            // Make the "Last Updated" timestamp readable too
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            lastUpdatedLabel.setText(LocalDate.now().format(dtf));
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
            // 1. Update State
            AppContext.getInstance().setSelectedReportId(record.getReportId());

            // 2. Log Activity
            int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();
            if (doctorId != 0) {
                DoctorDashboardDAO dashboardDAO = new DoctorDashboardDAO();
                dashboardDAO.logOpenedReport(doctorId, record.getReportId());
            }

            // 3. Navigate
            SceneLoader.switchScene(
                    recordsTable, // The TableView acts as the source node
                    "org/example/daibetes/modules/doctor/ui/review",
                    "doctorViewDiagnosis.fxml",
                    "Detailed Report",
                    null
            );
        }
    }