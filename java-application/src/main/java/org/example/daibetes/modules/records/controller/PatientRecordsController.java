package org.example.daibetes.modules.records.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientRecordsController {

    @FXML private TableView<ReportData> patientRecordsTable;
    @FXML private TableColumn<ReportData, String> colDate;
    @FXML private TableColumn<ReportData, String> colType;
    @FXML private TableColumn<ReportData, String> colDiagnosis;
    @FXML private TableColumn<ReportData, String> colCriticality;
    @FXML private TableColumn<ReportData, Void> colActions;
    @FXML private TableColumn<ReportData, String> colDoctor;

    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker datePicker;
    @FXML private Label lblLastUpdated;

    private final PatientDashboardDAO dao = new PatientDashboardDAO();
    private final ObservableList<ReportData> masterData = FXCollections.observableArrayList();
    private int patientId;

    @FXML
    public void initialize() {
        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser instanceof Patient p) {
            this.patientId = p.getPId();
        }

        setupTable();
        loadRealPatientData();

        statusFilter.getItems().setAll("All Results", "No DR", "Mild", "Moderate", "Severe", "Urgent");
        statusFilter.getSelectionModel().select(0);
        statusFilter.setOnAction(e -> applyFilters());
        datePicker.setOnAction(e -> applyFilters());
    }

    private void setupTable() {
        // 1. Date Column
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClientNotes()));
        colDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    try {
                        DateTimeFormatter in = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                        setText(LocalDateTime.parse(item, in).format(out));
                    } catch (Exception e) { setText(item); }
                }
            }
        });

        // 2. Consulting Doctor Column (Was empty in your screenshot)
        colDoctor.setCellValueFactory(data -> new SimpleStringProperty("Dr. " + data.getValue().getDoctorName()));

        // 3. Scan Type Column (Fixed from overwriting)
        colType.setCellValueFactory(data -> new SimpleStringProperty("Retinal Fundus Analysis"));

        // 4. Primary Diagnosis Column
        colDiagnosis.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDrGrade()));

        // 5. Criticality Column (Now maps to the severity level)
        colCriticality.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCriticality()));

        // 6. Action Button
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View Report");
            {
                btn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    ReportData data = getTableView().getItems().get(getIndex());
                    AppContext.getInstance().setSelectedReportId(data.getReportId());
                    SceneLoader.switchScene(patientRecordsTable, "org/example/daibetes/modules/records/controller", "patienViewDiagnosis.fxml", "Report Analysis", null);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void loadRealPatientData() {
        masterData.clear();
        List<String[]> rows = dao.getDiagnosesByPatient(patientId);
        for (String[] row : rows) {
            ReportData rd = new ReportData();
            rd.setReportId(Integer.parseInt(row[0]));
            rd.setDoctorName(row[1]);   // Physician Name
            rd.setCriticality(row[2]);  // High/Absent (Criticality)
            rd.setDrGrade(row[3]);      // The actual Grade from tblpathological
            rd.setClientNotes(row[4]);  // Date string
            masterData.add(rd);
        }
        patientRecordsTable.setItems(masterData);
    }
    private void applyFilters() {
        String s = statusFilter.getValue();
        java.time.LocalDate d = datePicker.getValue();
        patientRecordsTable.setItems(masterData.filtered(i ->
                (s.equals("All Results") || i.getDrGrade().contains(s)) &&
                        (d == null || i.getClientNotes().startsWith(d.toString()))
        ));
    }

    @FXML private void handleRefresh() { loadRealPatientData(); }
    @FXML private void handleClearFilters() { datePicker.setValue(null); statusFilter.getSelectionModel().select(0); patientRecordsTable.setItems(masterData); }
    @FXML private void handleBack(ActionEvent e) { SceneLoader.switchScene((Node) e.getSource(), "org/example/daibetes/modules/patient/dashboard", "patients-dashboard.fxml", "Dashboard", null); }
}