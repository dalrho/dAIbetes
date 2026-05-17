package org.example.daibetes.modules.doctor.ui.review.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ReportDataDAO;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;
import org.example.daibetes.shared.ui.SceneLoader;

public class DoctorViewDiagnosisController {

    private final SceneLoader sceneLoader = new SceneLoader();

    @FXML private ImageView reportImageView;

    @FXML private Label lblFinalCriticality;
    @FXML private Label lblReasoning;
    @FXML private Label lblNotes;

    @FXML private Label valMA;
    @FXML private Label valHem;
    @FXML private Label valExu;
    @FXML private Label valCWS;
    @FXML private Label valME;
    @FXML private Label valVB;
    @FXML private Label valIRMA;
    @FXML private Label valNV;
    @FXML private Label valVH;
    @FXML private Label valRD;
    @FXML private Label valDRGrade;
    @FXML private Label valDME;

    @FXML private FlowPane recommendationsContainer;
    @FXML private Button editDiagnosisBtn;

    @FXML
    public void initialize() {
        int reportId = AppContext.getInstance().getSelectedReportId();

        if (reportId == 0) {
            showAlert("Missing Report", "No report was selected.");
            return;
        }

        styleClinicalValues();
        loadReportData(reportId);
    }

    private void loadReportData(int reportId) {
        ReportDataDAO dao = new ReportDataDAO();
        ReportData reportData = dao.getReportDataByReportId(reportId);

        if (reportData == null) {
            showAlert("Load Failed", "Could not load the selected report.");
            return;
        }

        if (reportData.getScanImage() != null) {
            reportImageView.setImage(reportData.getScanImage());
        }

        lblFinalCriticality.setText(nullToDash(reportData.getCriticality()));
        lblReasoning.setText(nullToDash(reportData.getCriticalityReasoning()));

        valMA.setText(nullToDash(reportData.getMicroaneurysms()));
        valHem.setText(nullToDash(reportData.getHemorrhages()));
        valExu.setText(nullToDash(reportData.getHardExudates()));
        valCWS.setText(nullToDash(reportData.getCottonWoolSpots()));
        valME.setText(nullToDash(reportData.getMacularEdema()));
        valVB.setText(nullToDash(reportData.getVenousBeading()));

        valIRMA.setText(nullToDash(reportData.getIrma()));
        valNV.setText(nullToDash(reportData.getNeovascularization()));
        valVH.setText(nullToDash(reportData.getVitreousHemorrhage()));
        valRD.setText(nullToDash(reportData.getRetinalDetachment()));

        valDRGrade.setText(nullToDash(reportData.getDrGrade()));
        valDME.setText(nullToDash(reportData.getDmeGrade()));

        lblNotes.setText(nullToDash(reportData.getClinicalNotes()));

        loadRecommendations(reportData);
    }

    private void loadRecommendations(ReportData reportData) {
        recommendationsContainer.getChildren().clear();

        if (reportData.getRecommendations() != null) {
            for (String recommendation : reportData.getRecommendations()) {
                addRecommendationChip(recommendation);
            }
        }

        if (recommendationsContainer.getChildren().isEmpty()) {
            addRecommendationChip("No recommendations selected");
        }
    }

    private void addRecommendationChip(String text) {
        Label chip = new Label(text);

        chip.setStyle(
                "-fx-background-color: #E0F2FE;" +
                        "-fx-text-fill: #075985;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 8 12;" +
                        "-fx-background-radius: 20;" +
                        "-fx-font-size: 12px;"
        );

        recommendationsContainer.getChildren().add(chip);
    }

    private String nullToDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    @FXML
    private void handleEditDiagnosis(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        Scene scene = sceneLoader.load(
                "org/example/daibetes/modules/doctor/ui/report",
                "edit-generate-report.fxml",
                null
        );

        if (scene != null) {
            stage.setScene(scene);
            stage.setTitle("Edit Diagnosis");
            stage.show();
        }
    }

    @FXML
    private void handleExportPDF() {
        // TODO: Add PDF export logic here
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Scene scene = sceneLoader.load(
                "org/example/daibetes/modules/records/controller",
                "records-screen.fxml",
                null
        );

        if (scene != null) {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.setTitle("Patient Records");
            stage.show();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void styleClinicalValues() {
        Label[] labels = {
                valMA, valHem, valExu, valCWS, valME, valVB,
                valIRMA, valNV, valVH, valRD, valDRGrade, valDME
        };

        for (Label label : labels) {
            if (label != null) {
                label.setStyle(
                        "-fx-font-weight: bold;" +
                                "-fx-font-size: 16px;" +
                                "-fx-text-fill: #3B82F6;"
                );
            }
        }
    }
}