package records;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class RecordsController implements Initializable {


    @FXML
    private Button addRecordButton;

    @FXML
    private Button exportButton;

    @FXML
    private Button refreshButton;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusFilter;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private Button clearFiltersButton;

    @FXML
    private TableView<ScanRecord> recordsTable;

    @FXML
    private TableColumn<ScanRecord, String> recordIdColumn;

    @FXML
    private TableColumn<ScanRecord, String> patientNameColumn;

    @FXML
    private TableColumn<ScanRecord, String> patientIdColumn;

    @FXML
    private TableColumn<ScanRecord, String> scanDateColumn;

    @FXML
    private TableColumn<ScanRecord, String> scanTypeColumn;

    @FXML
    private TableColumn<ScanRecord, String> statusColumn;

    @FXML
    private TableColumn<ScanRecord, String> diagnosisColumn;

    @FXML
    private TableColumn<ScanRecord, String> actionsColumn;

    @FXML
    private Label recordCountLabel;

    @FXML
    private Label pageIndicatorLabel;

    @FXML
    private ComboBox<Integer> recordsPerPageCombo;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    private Label statusBarLabel;

    @FXML
    private Label lastUpdatedLabel;


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTableColumns();
        initializeFilters();
        initializeEventHandlers();
        loadSampleData();
        updateLastModifiedTime();
    }


    private void initializeTableColumns() {
        recordIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        patientNameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        patientIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        scanDateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        scanTypeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        statusColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        diagnosisColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));

        actionsColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty("--"));
    }


    private void initializeFilters() {
        statusFilter.getItems().addAll(
                "All Records",
                "Pending",
                "Analyzed",
                "Approved",
                "Archived"
        );
        statusFilter.getSelectionModel().selectFirst();

        recordsPerPageCombo.getItems().addAll(10, 25, 50, 100);
        recordsPerPageCombo.getSelectionModel().selectFirst();

        fromDatePicker.setValue(LocalDate.now().minusMonths(3));
        toDatePicker.setValue(LocalDate.now());
    }

    private void initializeEventHandlers() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleSearch();
        });

        statusFilter.setOnAction(event -> {
            handleFilterChange();
        });

        fromDatePicker.setOnAction(event -> {
            handleFilterChange();
        });

        toDatePicker.setOnAction(event -> {
            handleFilterChange();
        });

        clearFiltersButton.setOnAction(event -> {
            handleClearFilters();
        });

        previousButton.setOnAction(event -> {
            handlePreviousPage();
        });

        nextButton.setOnAction(event -> {
            handleNextPage();
        });

        recordsPerPageCombo.setOnAction(event -> {
            handleRecordsPerPageChange();
        });

        addRecordButton.setOnAction(event -> {
            handleAddRecord();
        });

        exportButton.setOnAction(event -> {
            handleExport();
        });

        refreshButton.setOnAction(event -> {
            handleRefresh();
        });
    }

    @FXML
    private void handleSearch() {
        updateStatusBar("Searching records...");
    }

    @FXML
    private void handleFilterChange() {
        updateStatusBar("Applying filters...");
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        statusFilter.getSelectionModel().selectFirst();
        fromDatePicker.setValue(LocalDate.now().minusMonths(3));
        toDatePicker.setValue(LocalDate.now());
        updateStatusBar("Filters cleared");
        loadSampleData();
    }

    @FXML
    private void handleAddRecord() {
        updateStatusBar("Opening new record dialog...");
        // Implementation would open a dialog to create a new record
        showInfo("New Record", "New record creation would be initiated here");
    }

    @FXML
    private void handleExport() {
        updateStatusBar("Exporting records...");
        // Implementation would export selected records to CSV/PDF
        showInfo("Export", "Records would be exported to CSV/PDF format");
    }

    @FXML
    private void handleRefresh() {
        updateStatusBar("Refreshing records from database...");
        loadSampleData();
        updateLastModifiedTime();
        showInfo("Refresh", "Records refreshed successfully");
    }


    @FXML
    private void handlePreviousPage() {
        updateStatusBar("Loading previous page...");
        // Implementation would load previous page of records
    }


    @FXML
    private void handleNextPage() {
        updateStatusBar("Loading next page...");
        // Implementation would load next page of records
    }


    @FXML
    private void handleRecordsPerPageChange() {
        Integer selectedValue = recordsPerPageCombo.getSelectionModel().getSelectedItem();
        if (selectedValue != null) {
            updateStatusBar("Updating records per page to " + selectedValue);
            // Implementation would reload table with new page size
        }
    }


    @FXML
    private void handlePrimaryButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-font-size: 11; -fx-padding: 10 20; -fx-background-color: #0052A3; -fx-text-fill: WHITE; -fx-font-weight: bold; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
    }


    @FXML
    private void handlePrimaryButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-font-size: 11; -fx-padding: 10 20; -fx-background-color: #0066CC; -fx-text-fill: WHITE; -fx-font-weight: bold; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
    }


    @FXML
    private void handleButtonHover(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-font-size: 11; -fx-padding: 10 18; -fx-background-color: #E5E7EB; -fx-text-fill: #1F2937; -fx-border-color: #D1D5DB; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
    }


    @FXML
    private void handleButtonExit(MouseEvent event) {
        Button button = (Button) event.getSource();
        button.setStyle("-fx-font-size: 11; -fx-padding: 10 18; -fx-background-color: #F3F4F6; -fx-text-fill: #374151; -fx-border-color: #D1D5DB; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;");
    }


    private void loadSampleData() {
        // Clear existing data
        recordsTable.getItems().clear();

        // Add sample records (placeholder implementation)
        recordsTable.getItems().add(new ScanRecord());
        recordsTable.getItems().add(new ScanRecord());
        recordsTable.getItems().add(new ScanRecord());

        updateRecordCount();
    }

    private void updateRecordCount() {
        int totalRecords = recordsTable.getItems().size();
        recordCountLabel.setText(String.format("Showing %d of %d records", totalRecords, totalRecords));
        pageIndicatorLabel.setText("Page 1 of 1");
    }

    private void updateStatusBar(String message) {
        statusBarLabel.setText(message);
    }

    private void updateLastModifiedTime() {
        lastUpdatedLabel.setText(LocalDate.now().format(DATE_FORMATTER) + " " +
                java.time.LocalTime.now().format(TIME_FORMATTER));
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ScanRecord {
        private String recordId;
        private String patientName;
        private String patientId;
        private LocalDate scanDate;
        private String scanType;
        private String status;
        private String diagnosis;

        public ScanRecord() {
            this.recordId = "REC-001";
            this.patientName = "John Doe";
            this.patientId = "PAT-001";
            this.scanDate = LocalDate.now();
            this.scanType = "Retinal";
            this.status = "Analyzed";
            this.diagnosis = "Healthy";
        }

        public String getRecordId() { return recordId; }
        public String getPatientName() { return patientName; }
        public String getPatientId() { return patientId; }
        public LocalDate getScanDate() { return scanDate; }
        public String getScanType() { return scanType; }
        public String getStatus() { return status; }
        public String getDiagnosis() { return diagnosis; }
    }
}