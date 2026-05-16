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
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;
import java.util.ArrayList;
import java.util.Arrays;

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

    @FXML
    public void initialize() {
        setupTable();
        loadPatientData();

        statusFilter.getItems().addAll("All Results", "No DR", "Mild", "Moderate", "Severe", "Urgent");
    }

    private void setupTable() {
        // Cell Value Factories mapping to ReportData fields
        // Since ReportData uses standard Strings, we wrap them in SimpleStringProperty for the TableView

        colDate.setCellValueFactory(cellData -> new SimpleStringProperty("2024-05-15")); // Mock fixed date

        colType.setCellValueFactory(cellData -> new SimpleStringProperty("Retinal Fundus"));

        colDiagnosis.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDrGrade()));

        colCriticality.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCriticality()));

        // Create the "View Report" button for every row
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("View Report");
            {
                viewBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; " +
                        "-fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 5;");
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

    private void loadPatientData() {
        ObservableList<ReportData> mockData = FXCollections.observableArrayList();

        // --- MOCK RECORD 1: URGENT ---
        ReportData r1 = new ReportData();
        r1.setPatientName("Current User");
        r1.setDrGrade("Proliferative DR (PDR)");
        r1.setCriticality("Urgent");
        r1.setCriticalityReasoning("Extensive neovascularization observed. Immediate specialist intervention required to prevent vision loss.");
        r1.setMicroaneurysms("Severe");
        r1.setHemorrhages("Extensive");
        r1.setHardExudates("Macular");
        r1.setMacularEdema("Present");
        r1.setRecommendations(new ArrayList<>(Arrays.asList("Urgent Evaluation", "Laser Treatment", "Anti-VEGF Therapy")));
        r1.setClinicalNotes("Patient advised to avoid heavy lifting until surgery.");

        // --- MOCK RECORD 2: MODERATE ---
        ReportData r2 = new ReportData();
        r2.setPatientName("Current User");
        r2.setDrGrade("Moderate NPDR (Grade 2)");
        r2.setCriticality("Moderate");
        r2.setCriticalityReasoning("Multiple microaneurysms and few hemorrhages. Condition is stable but requires monitoring.");
        r2.setMicroaneurysms("Moderate");
        r2.setHemorrhages("Few");
        r2.setHardExudates("Present");
        r2.setMacularEdema("Absent");
        r2.setRecommendations(new ArrayList<>(Arrays.asList("6-month Follow-up", "Strict Blood Sugar Control")));
        r2.setClinicalNotes("Improvement seen in blood pressure management.");

        // --- MOCK RECORD 3: HEALTHY ---
        ReportData r3 = new ReportData();
        r3.setPatientName("Current User");
        r3.setDrGrade("No DR (Grade 0)");
        r3.setCriticality("Low");
        r3.setCriticalityReasoning("No pathological signs detected in the retina. Macula is clear.");
        r3.setMicroaneurysms("Absent");
        r3.setHemorrhages("Absent");
        r3.setHardExudates("Absent");
        r3.setMacularEdema("Absent");
        r3.setRecommendations(new ArrayList<>(Arrays.asList("Annual Follow-up")));
        r3.setClinicalNotes("Routine screening complete.");

        mockData.addAll(r1, r2, r3);
        patientRecordsTable.setItems(mockData);
    }

    private void handleViewDetails(ReportData data) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/daibetes/modules/doctor/ui/review/review_results.fxml")
            );

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("View Diagnosis");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadPatientData();
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

        if (scene == null) {
            System.out.println("Failed to load Patient Calendar screen");
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Schedule Follow-up");
    }
}