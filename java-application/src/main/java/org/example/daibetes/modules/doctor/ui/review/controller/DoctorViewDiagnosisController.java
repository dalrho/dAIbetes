package org.example.daibetes.modules.doctor.ui.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.daibetes.modules.records.model.Record; // Assuming Record class is in records package
import org.example.daibetes.shared.ui.SceneLoader;

public class DoctorViewDiagnosisController {

    @FXML private ImageView reportImageView;

    // Status Labels
    @FXML private Label lblFinalCriticality;
    @FXML private Label lblReasoning;
    @FXML private Label lblNotes;
    // Clinical Summary Labels
    @FXML private Label valMA, valHem, valExu, valCWS, valME, valVB, valDRGrade, valDME;
    @FXML private Label valIRMA;
    @FXML private Label valNV;
    @FXML private Label valVH;
    @FXML private Label valRD;

    @FXML private FlowPane recommendationsContainer;
    @FXML private Button editDiagnosisBtn;

    private boolean isEditMode = false;
    private Record currentRecord;

    /**
     * This method is called by the RecordsController
     * to pass the selected record data to this view.
     */
    public void setRecordData(Record record) {
        this.currentRecord = record;

        lblFinalCriticality.setText(record.getCriticalityLevel());
        lblReasoning.setText("Criticality Level: " + record.getCriticalityLevel());

        valDRGrade.setText(record.getCriticalityLevel().equals("N/A") ? "Pending" : "Grade 2");

        lblNotes.setText("Reviewing record for " + record.getPatientName());
    }

    @FXML
    private void handleEditDiagnosis(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                SceneLoader.load(
                        "org/example/daibetes/modules/doctor/ui/report",
                        "edit-generate-report.fxml",
                        null
                )
        );
    }

    @FXML
    private void handleExportPDF() {
        System.out.println("Exporting report to PDF...");
        // PDF Export Logic
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/records/controller",
                "records-screen.fxml",
                null
        );

        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }
}