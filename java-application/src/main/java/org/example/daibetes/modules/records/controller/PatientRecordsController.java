package org.example.daibetes.modules.records.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.PatientDashboardDAO;
import org.example.daibetes.shared.models.Patient;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatientRecordsController {

    @FXML private TableView<ReportData> patientRecordsTable;
    @FXML private TableColumn<ReportData, String> colDate;
    @FXML private TableColumn<ReportData, String> colType;
    @FXML private TableColumn<ReportData, String> colDiagnosis;
    @FXML private TableColumn<ReportData, String> colCriticality;
    @FXML private TableColumn<ReportData, Void> colActions;

    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker datePicker;
    @FXML private Label lblLastUpdated;

    private final PatientDashboardDAO dao = new PatientDashboardDAO();
    private int patientId;

    @FXML
    public void initialize() {
        // Resolve the active session context parameter safely upon initialization pass
        User currentUser = AppContext.getInstance().getCurrentUser();
        if (currentUser instanceof Patient p) {
            this.patientId = p.getPId();
        } else {
            System.err.println("PatientRecordsController Error: Missing active patient profile session mapping bounds.");
        }

        setupTable();
        loadRealPatientData();

        statusFilter.getItems().setAll("All Results", "No DR", "Mild", "Moderate", "Severe", "Urgent");
        statusFilter.getSelectionModel().select(0);
    }

    private void setupTable() {
        // Tie custom programmatic string access properties back onto underlying table cell layout definitions
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientNotes()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty("Retinal Fundus Analysis"));
        colDiagnosis.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDrGrade()));
        colCriticality.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCriticality()));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View Report");
            {
                viewBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
                viewBtn.setOnAction(event -> {
                    ReportData data = getTableView().getItems().get(getIndex());
                    handleViewDetails(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(viewBtn);
            }
        });
    }

    private void loadRealPatientData() {
        ObservableList<ReportData> liveReportList = FXCollections.observableArrayList();
        if (patientId <= 0) return;

        // Pull active historical database arrays: [1]=Doctor Name, [2]=Diagnosis Text, [3]=Recommendation, [4]=Date String
        List<String[]> rows = dao.getDiagnosesByPatient(patientId);

        for (String[] row : rows) {
            ReportData recordNode = new ReportData();
            recordNode.setPatientName("Dr. " + row[1]); // Temp storage tracking for programmatic text views
            recordNode.setDrGrade(row[2]);             // Assessment data mappings field
            recordNode.setCriticality("Assigned");       // Fixed structural state fallback metric

            // Re-purposing data nodes safely to transport explicit fields through your structural model setup
            recordNode.setClientNotes(row[4]);           // Passing standard date text
            recordNode.setClinicalNotes(row[3]);         // Passing standard prescription treatment plan notes

            liveReportList.add(recordNode);
        }

        patientRecordsTable.setItems(liveReportList);
    }

    private void handleViewDetails(ReportData data) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/daibetes/modules/records/controller/review_results.fxml")
            );
            Parent root = loader.load();

            // Optional dependency injection logic snippet mapping values down into details controllers goes here

            Stage stage = new Stage();
            stage.setTitle("Detailed Diagnostic View Analysis");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadRealPatientData();
        lblLastUpdated.setText(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    private void handleClearFilters() {
        datePicker.setValue(null);
        statusFilter.getSelectionModel().select(0);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/patient/dashboard",
                "patients-dashboard.fxml",
                null
        );
        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("dAIbetes — Patient Dashboard Workspace");
        }
    }
}