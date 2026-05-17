package org.example.daibetes.modules.doctor.ui.report.controller;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ReportDataDAO;
import org.example.daibetes.modules.ai.dto.AIResponseDTO;
import org.example.daibetes.modules.ai.service.AIInferenceService;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.shared.utils.AppExecutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

public class EditGenerateResultsController {

    @FXML private ImageView reportImageView;
    @FXML private Label aiCriticalityLabel;
    @FXML private Label patientNameLabel; // ADDED

    @FXML private ComboBox<String> criticalityCombo;
    @FXML private TextArea doctorCriticalityArea;

    @FXML private ToggleGroup tgMA;
    @FXML private ToggleGroup tgHem;
    @FXML private ToggleGroup tgExu;
    @FXML private ToggleGroup tgCWS;
    @FXML private ToggleGroup tgME;
    @FXML private ToggleGroup tgVB;
    @FXML private ToggleGroup tgIRMA;
    @FXML private ToggleGroup tgNV;
    @FXML private ToggleGroup tgVH;
    @FXML private ToggleGroup tgRD;

    @FXML private ToggleGroup tgDR;
    @FXML private ToggleGroup tgDME;

    @FXML private CheckBox annualFollowupCheck;
    @FXML private CheckBox sixMonthCheck;
    @FXML private CheckBox referCheck;
    @FXML private CheckBox urgentCheck;
    @FXML private CheckBox laserCheck;
    @FXML private CheckBox antiVegfCheck;
    @FXML private TextArea notesArea;

    @FXML private Label aiConfidenceLabel, aiExplanationLabel;
    @FXML private VBox aiResultBox;
    @FXML private ProgressIndicator aiLoader;

    @FXML
    public void initialize() {
        // ADDED: Initialize Patient Name
        String patientName = AppContext.getInstance().getSelectedRecordsPatientName();
        if (patientName == null || patientName.isBlank()) {
            patientName = AppContext.getInstance().getSelectedPatientName();
        }
        if (patientName != null && !patientName.isBlank()) {
            patientNameLabel.setText(patientName);
        }

        criticalityCombo.getItems().setAll(
                "Absent",
                "Low",
                "Moderate",
                "High",
                "Critical"
        );

        int reportId = AppContext.getInstance().getSelectedReportId();

        if (reportId == 0) {
            showAlert("Missing Report", "No report was selected.");
            return;
        }

        loadExistingReport(reportId);
    }

    private void loadExistingReport(int reportId) {
        ReportDataDAO dao = new ReportDataDAO();
        ReportData data = dao.getReportDataByReportId(reportId);

        if (data == null) {
            showAlert("Load Failed", "Could not load report data.");
            return;
        }

        if (data.getScanImage() != null) {
            reportImageView.setImage(data.getScanImage());
        }

        if (data.getCriticality() != null &&
                !criticalityCombo.getItems().contains(data.getCriticality())) {
            criticalityCombo.getItems().add(data.getCriticality());
        }

        criticalityCombo.setValue(data.getCriticality());
        doctorCriticalityArea.setText(data.getCriticalityReasoning());

        selectToggleByText(tgMA, data.getMicroaneurysms());
        selectToggleByText(tgHem, data.getHemorrhages());
        selectToggleByText(tgExu, data.getHardExudates());
        selectToggleByText(tgCWS, data.getCottonWoolSpots());
        selectToggleByText(tgME, data.getMacularEdema());
        selectToggleByText(tgVB, data.getVenousBeading());
        selectToggleByText(tgIRMA, data.getIrma());
        selectToggleByText(tgNV, data.getNeovascularization());
        selectToggleByText(tgVH, data.getVitreousHemorrhage());
        selectToggleByText(tgRD, data.getRetinalDetachment());

        selectToggleByText(tgDR, data.getDrGrade());
        selectToggleByText(tgDME, data.getDmeGrade());

        if (data.getRecommendations() != null) {
            annualFollowupCheck.setSelected(data.getRecommendations().contains("Annual Follow-up"));
            sixMonthCheck.setSelected(data.getRecommendations().contains("6-month Follow-up"));
            referCheck.setSelected(data.getRecommendations().contains("Refer to Specialist"));
            urgentCheck.setSelected(data.getRecommendations().contains("Urgent Evaluation"));
            laserCheck.setSelected(data.getRecommendations().contains("Laser Treatment"));
            antiVegfCheck.setSelected(data.getRecommendations().contains("Anti-VEGF Therapy"));
        }

        notesArea.setText(data.getClinicalNotes());
    }

    private void selectToggleByText(ToggleGroup group, String value) {
        if (group == null || value == null) return;
        for (Toggle toggle : group.getToggles()) {
            if (toggle instanceof RadioButton radioButton) {
                if (radioButton.getText().equalsIgnoreCase(value.trim())) {
                    group.selectToggle(radioButton);
                    return;
                }
            }
        }
    }

    @FXML
    private void handleAIDetection() {
        aiLoader.setVisible(true);
        aiCriticalityLabel.setText("Analyzing...");
        aiConfidenceLabel.setText("");
        aiExplanationLabel.setText("Running AI inference...");

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try {
                    File imageFile = AppContext.getInstance().getSelectedImageFile();
                    if (imageFile == null) {
                        javafx.application.Platform.runLater(() -> {
                            aiLoader.setVisible(false);
                            aiExplanationLabel.setText("No retinal image selected.");
                        });
                        return null;
                    }

                    AIInferenceService aiService = new AIInferenceService();
                    AIResponseDTO response = aiService.analyzeImage(imageFile);

                    javafx.application.Platform.runLater(() -> {
                        aiLoader.setVisible(false);
                        aiCriticalityLabel.setText(response.getPrediction().getPredicted_class());
                        double confidence = response.getPrediction().getConfidence() * 100;
                        aiConfidenceLabel.setText(String.format("%.2f%%", confidence));
                        aiExplanationLabel.setText(response.getClinical_guidance());

                        aiResultBox.setStyle("-fx-background-color: #F0FDF4; -fx-background-radius: 12; -fx-border-color: #BBF7D0; -fx-border-width: 1; -fx-border-radius: 12;");
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        aiLoader.setVisible(false);
                        aiCriticalityLabel.setText("ERROR");
                        aiExplanationLabel.setText("AI analysis failed.");
                    });
                }
                return null;
            }
        };
        AppExecutor.get().submit(task);
    }

    private String getSelected(ToggleGroup group) {
        if (group.getSelectedToggle() != null) {
            RadioButton rb = (RadioButton) group.getSelectedToggle();
            return rb.getText();
        }
        return "Not Selected";
    }

    @FXML
    private void handleSaveReport() {
        int reportId = AppContext.getInstance().getSelectedReportId();

        if (reportId == 0) {
            showAlert("Missing Report", "No report was selected for editing.");
            return;
        }

        String microaneurysms = getSelected(tgMA);
        String hemorrhages = getSelected(tgHem);
        String exudates = getSelected(tgExu);
        String cottonWoolSpots = getSelected(tgCWS);
        String macularEdemaFinding = getSelected(tgME);
        String venousBeading = getSelected(tgVB);
        String irma = getSelected(tgIRMA);
        String neovascularization = getSelected(tgNV);
        String vitreousHemorrhage = getSelected(tgVH);
        String retinalDetachment = getSelected(tgRD);

        String drGrade = getSelected(tgDR);
        String dmeGrade = getSelected(tgDME);

        String criticality = criticalityCombo.getValue();

        String doctorReasoning = doctorCriticalityArea.getText() == null
                ? ""
                : doctorCriticalityArea.getText().trim();

        String notes = notesArea.getText() == null
                ? ""
                : notesArea.getText().trim();

        if (reportImageView.getImage() == null) {
            showAlert("Missing Image", "Please make sure a report image is loaded before saving.");
            return;
        }

        if (isBlank(microaneurysms) || isBlank(hemorrhages) || isBlank(exudates) ||
                isBlank(cottonWoolSpots) || isBlank(macularEdemaFinding) || isBlank(venousBeading) ||
                isBlank(irma) || isBlank(neovascularization) || isBlank(vitreousHemorrhage) ||
                isBlank(retinalDetachment) || isBlank(drGrade) || isBlank(dmeGrade) ||
                isBlank(criticality) || isBlank(doctorReasoning) || !hasAtLeastOneRecommendation() || isBlank(notes)) {
            showAlert("Missing Data", "Please fill in all findings, evaluations, and clinical notes.");
            return;
        }

        try {
            ReportDataDAO reportDataDAO = new ReportDataDAO();
            ReportDataDAO.ReportRefs refs = reportDataDAO.getReportRefsByReportId(reportId);

            if (refs == null) {
                showAlert("Save Failed", "Could not find the existing report references.");
                return;
            }

            File reportImageFile = imageViewToTempFile(reportImageView);
            if (reportImageFile != null && reportImageFile.exists()) {
                byte[] imageBytes = java.nio.file.Files.readAllBytes(reportImageFile.toPath());
                reportDataDAO.updateImage(refs.getRawImageId(), imageBytes);
            }

            reportDataDAO.updateCriticality(refs.getCriticalityId(), criticality, doctorReasoning);
            reportDataDAO.updatePathologicalFindings(refs.getFindingsId(), microaneurysms, hemorrhages, exudates, cottonWoolSpots, macularEdemaFinding, venousBeading, irma, neovascularization, vitreousHemorrhage, retinalDetachment);
            reportDataDAO.updateEvaluation(refs.getEvaluationId(), drGrade, dmeGrade);
            reportDataDAO.updateRecommendations(refs.getRecommendationsId(), annualFollowupCheck.isSelected(), sixMonthCheck.isSelected(), referCheck.isSelected(), urgentCheck.isSelected(), laserCheck.isSelected(), antiVegfCheck.isSelected(), notes);
            reportDataDAO.updateReportSavedOn(reportId);

            showAlert("Success", "Report updated successfully.");
            handleBack();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save Error", "Something went wrong while updating the report.");
        }
    }

    private File imageViewToTempFile(ImageView imageView) {
        try {
            if (imageView.getImage() == null) return null;
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
            File tempFile = File.createTempFile("report_image_", ".png");
            tempFile.deleteOnExit();
            ImageIO.write(bufferedImage, "png", tempFile);
            return tempFile;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty() || value.equals("Not Selected");
    }

    private boolean hasAtLeastOneRecommendation() {
        return annualFollowupCheck.isSelected() || sixMonthCheck.isSelected() || referCheck.isSelected() ||
                urgentCheck.isSelected() || laserCheck.isSelected() || antiVegfCheck.isSelected();
    }

    @FXML
    private void handleBack() {
        Scene scene = SceneLoader.load("org/example/daibetes/modules/doctor/ui/review", "doctorViewDiagnosis.fxml", null);
        if (scene != null) {
            Stage stage = (Stage) notesArea.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Detailed Report");
            stage.show();
        }
    }
}