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
import org.example.daibetes.modules.ai.dto.AIResponseDTO;
import org.example.daibetes.modules.ai.service.AIInferenceService;
import org.example.daibetes.shared.ui.SceneLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;
import org.example.daibetes.core.database.ReportDataDAO;
import org.example.daibetes.modules.doctor.ui.review.model.ReportData ;

import java.io.File;

public class EditGenerateResultsController {

    @FXML private ImageView reportImageView;

    @FXML private Label aiCriticalityLabel;

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


    @FXML private Label  aiConfidenceLabel, aiExplanationLabel;
    @FXML private VBox aiResultBox;
    @FXML private ProgressIndicator aiLoader;


    private Image passedImage;
    @FXML
    public void initialize() {
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
        if (group == null || value == null) {
            return;
        }

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

                        aiLoader.setVisible(false);

                        aiExplanationLabel.setText(
                                "No retinal image selected."
                        );

                        return null;
                    }

                    AIInferenceService aiService =
                            new AIInferenceService();

                    AIResponseDTO response =
                            aiService.analyzeImage(imageFile);

                    javafx.application.Platform.runLater(() -> {

                        aiLoader.setVisible(false);

                        aiCriticalityLabel.setText(
                                response.getPrediction()
                                        .getPredicted_class()
                        );

                        double confidence =
                                response.getPrediction()
                                        .getConfidence() * 100;

                        aiConfidenceLabel.setText(
                                String.format("%.2f%%", confidence)
                        );

                        aiExplanationLabel.setText(
                                response.getClinical_guidance()
                        );

                        aiResultBox.setStyle("""
                        -fx-background-color: #F0FDF4;
                        -fx-background-radius: 12;
                        -fx-padding: 20;
                        -fx-border-color: #BBF7D0;
                    """);
                    });

                } catch (Exception e) {

                    e.printStackTrace();

                    javafx.application.Platform.runLater(() -> {

                        aiLoader.setVisible(false);

                        aiCriticalityLabel.setText("ERROR");

                        aiExplanationLabel.setText(
                                "AI analysis failed."
                        );
                    });
                }

                return null;
            }
        };

        new Thread(task).start();
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

        if (isBlank(microaneurysms)) {
            showAlert("Missing Finding", "Please select a value for Microaneurysms.");
            return;
        }

        if (isBlank(hemorrhages)) {
            showAlert("Missing Finding", "Please select a value for Hemorrhages.");
            return;
        }

        if (isBlank(exudates)) {
            showAlert("Missing Finding", "Please select a value for Hard Exudates.");
            return;
        }

        if (isBlank(cottonWoolSpots)) {
            showAlert("Missing Finding", "Please select a value for Cotton Wool Spots.");
            return;
        }

        if (isBlank(macularEdemaFinding)) {
            showAlert("Missing Finding", "Please select a value for Macular Edema.");
            return;
        }

        if (isBlank(venousBeading)) {
            showAlert("Missing Finding", "Please select a value for Venous Beading.");
            return;
        }

        if (isBlank(irma)) {
            showAlert("Missing Finding", "Please select a value for IRMA.");
            return;
        }

        if (isBlank(neovascularization)) {
            showAlert("Missing Finding", "Please select a value for Neovascularization.");
            return;
        }

        if (isBlank(vitreousHemorrhage)) {
            showAlert("Missing Finding", "Please select a value for Vitreous Hemorrhage.");
            return;
        }

        if (isBlank(retinalDetachment)) {
            showAlert("Missing Finding", "Please select a value for Retinal Detachment.");
            return;
        }

        if (isBlank(drGrade)) {
            showAlert("Missing Evaluation", "Please select a final DR grade.");
            return;
        }

        if (isBlank(dmeGrade)) {
            showAlert("Missing Evaluation", "Please select a macular edema grade.");
            return;
        }

        if (isBlank(criticality)) {
            showAlert("Missing Criticality", "Please select a criticality level.");
            return;
        }

        if (isBlank(doctorReasoning)) {
            showAlert("Missing Reasoning", "Please enter the doctor's criticality reasoning.");
            return;
        }

        if (!hasAtLeastOneRecommendation()) {
            showAlert("Missing Recommendation", "Please select at least one recommendation.");
            return;
        }

        if (isBlank(notes)) {
            showAlert("Missing Notes", "Please enter final notes.");
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

            if (reportImageFile == null || !reportImageFile.exists()) {
                showAlert("Image Error", "No valid report image file was created.");
                return;
            }

            byte[] imageBytes = java.nio.file.Files.readAllBytes(reportImageFile.toPath());

            boolean imageUpdated = reportDataDAO.updateImage(
                    refs.getRawImageId(),
                    imageBytes
            );

            if (!imageUpdated) {
                showAlert("Save Failed", "Image was not updated.");
                return;
            }

            boolean criticalityUpdated = reportDataDAO.updateCriticality(
                    refs.getCriticalityId(),
                    criticality,
                    doctorReasoning
            );

            if (!criticalityUpdated) {
                showAlert("Save Failed", "Criticality was not updated.");
                return;
            }

            boolean pathologicalUpdated = reportDataDAO.updatePathologicalFindings(
                    refs.getFindingsId(),
                    microaneurysms,
                    hemorrhages,
                    exudates,
                    cottonWoolSpots,
                    macularEdemaFinding,
                    venousBeading,
                    irma,
                    neovascularization,
                    vitreousHemorrhage,
                    retinalDetachment
            );

            if (!pathologicalUpdated) {
                showAlert("Save Failed", "Pathological findings were not updated.");
                return;
            }

            boolean evaluationUpdated = reportDataDAO.updateEvaluation(
                    refs.getEvaluationId(),
                    drGrade,
                    dmeGrade
            );

            if (!evaluationUpdated) {
                showAlert("Save Failed", "Evaluation was not updated.");
                return;
            }

            boolean recommendationsUpdated = reportDataDAO.updateRecommendations(
                    refs.getRecommendationsId(),
                    annualFollowupCheck.isSelected(),
                    sixMonthCheck.isSelected(),
                    referCheck.isSelected(),
                    urgentCheck.isSelected(),
                    laserCheck.isSelected(),
                    antiVegfCheck.isSelected(),
                    notes
            );

            if (!recommendationsUpdated) {
                showAlert("Save Failed", "Recommendations were not updated.");
                return;
            }

            boolean timestampUpdated = reportDataDAO.updateReportSavedOn(reportId);

            if (!timestampUpdated) {
                showAlert("Warning", "Report was updated, but saved_on was not updated.");
                return;
            }

            showAlert("Success", "Report updated successfully.");

            Stage stage = (Stage) reportImageView.getScene().getWindow();

            Scene scene = SceneLoader.load(
                    "org/example/daibetes/modules/records/controller",
                    "doctorViewDiagnosis.fxml",
                    null
            );

            if (scene == null) {
                showAlert("Navigation Error", "Could not return to the detailed report screen.");
                return;
            }

            stage.setScene(scene);
            stage.setTitle("Detailed Report");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save Error", "Something went wrong while updating the report.");
        }
    }
    private File imageViewToTempFile(ImageView imageView) {
        try {
            if (imageView.getImage() == null) {
                return null;
            }

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);

            File tempFile = File.createTempFile("report_image_", ".png");
            tempFile.deleteOnExit();

            ImageIO.write(bufferedImage, "png", tempFile);

            return tempFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean hasAtLeastOneRecommendation() {
        return annualFollowupCheck.isSelected()
                || sixMonthCheck.isSelected()
                || referCheck.isSelected()
                || urgentCheck.isSelected()
                || laserCheck.isSelected()
                || antiVegfCheck.isSelected();
    }




    @FXML
    private void handleBack() {
        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/records/controller",
                "doctorViewDiagnosis.fxml",
                null
        );

        if (scene == null) {
            showAlert("Navigation Error", "Could not return to the detailed report screen.");
            return;
        }

        Stage stage = (Stage) notesArea.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Detailed Report");
        stage.show();
    }
}