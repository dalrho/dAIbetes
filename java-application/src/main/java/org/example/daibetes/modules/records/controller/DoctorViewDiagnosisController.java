package org.example.daibetes.modules.records.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;

public class DoctorViewDiagnosisController {

    @FXML private Label lblPatientName, lblPatientId, lblVisitDate;
    @FXML private ImageView imgScan;
    @FXML private Label valMA, valHem, valExu, valME, valDRGrade, valCriticality, valReasoning;
    @FXML private FlowPane flowRecommendations;
    @FXML private ListView<String> listPreviousVisits;

    /**
     * Call this when navigating from Records table to this screen
     */
    public void setDiagnosisData(ReportData data) {
        lblPatientName.setText("Patient: " + data.getPatientName());
        lblPatientId.setText("ID: " + "P-10293"); // Example
        imgScan.setImage(data.getScanImage());

        valMA.setText(data.getMicroaneurysms());
        valHem.setText(data.getHemorrhages());
        valExu.setText(data.getHardExudates());
        valME.setText(data.getMacularEdema());

        valDRGrade.setText(data.getDrGrade());
        valCriticality.setText(data.getCriticality().toUpperCase());
        valReasoning.setText(data.getCriticalityReasoning());

        // Fill Recommendations
        flowRecommendations.getChildren().clear();
        for (String rec : data.getRecommendations()) {
            Label tag = new Label(rec);
            tag.setStyle("-fx-background-color: #E0F2FE; -fx-text-fill: #0369A1; " +
                    "-fx-padding: 5 12; -fx-background-radius: 15; -fx-font-weight: bold;");
            flowRecommendations.getChildren().add(tag);
        }

        // Mock History
        listPreviousVisits.getItems().addAll(
                "Visit: Dec 12, 2023 - Mild NPDR",
                "Visit: Aug 05, 2023 - No DR",
                "Visit: Feb 20, 2023 - No DR"
        );
    }

    @FXML
    private void handleBack() {
        // Use your scene loader to go back to records screen
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/patientDashboard/patients-dashboard.fxml")
            );

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Patient Dashboard");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExportPDF() {
        // Reuse your PDF export logic here
    }

    @FXML
    private void handleEdit() {
        // Navigate back to GenerateResultsController with this data pre-loaded
    }
}
