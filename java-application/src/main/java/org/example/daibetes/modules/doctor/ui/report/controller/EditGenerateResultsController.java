package org.example.daibetes.modules.doctor.ui.report.controller;

import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.database.ReportDAO;
import org.example.daibetes.core.database.TestDAO;
import org.example.daibetes.modules.ai.dto.AIResponseDTO;
import org.example.daibetes.modules.ai.service.AIInferenceService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

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
        // 1. Pull the image that was just saved in the previous step
         passedImage = AppContext.getInstance().getSelectedImage();

        if (passedImage != null) {
            // 2. Set it to the ImageView on the report screen
            reportImageView.setImage(passedImage);
        } else {
            System.err.println("Warning: No image was passed to the Report Generator.");
        }

        // 3. Populate your ComboBox
        if (criticalityCombo != null) {
            criticalityCombo.getItems().addAll("Absent", "Low", "Moderate", "High", "Critical");
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

        // =========================
        // STRICT VALIDATION FIRST
        // Do not save anything unless everything is complete.
        // =========================

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

        // =========================
        // SAVE ONLY AFTER ALL VALIDATION PASSES
        // =========================

        try {
            ImageDAO imageDAO = new ImageDAO();
            ReportDAO reportDAO = new ReportDAO();

            File reportImageFile = imageViewToTempFile(reportImageView);

            if (reportImageFile == null || !reportImageFile.exists()) {
                showAlert("Image Error", "No valid report image file was created.");
                return;
            }

            int imageId = imageDAO.createImage(reportImageFile, 1);

            if (imageId == -1) {
                showAlert("Image Save Failed", "Report image was not saved.");
                return;
            }

            int criticalityId = reportDAO.createCriticality(
                    criticality,
                    doctorReasoning
            );

            if (criticalityId == -1) {
                showAlert("Save Failed", "Criticality was not saved.");
                return;
            }

            int findingsId = reportDAO.createPathologicalFindings(
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

            if (findingsId == -1) {
                showAlert("Save Failed", "Pathological findings were not saved.");
                return;
            }

            int evaluationId = reportDAO.createEvaluation(
                    drGrade,
                    dmeGrade
            );

            if (evaluationId == -1) {
                showAlert("Save Failed", "Evaluation was not saved.");
                return;
            }

            int recommendationsId = reportDAO.createRecommendations(
                    annualFollowupCheck.isSelected(),
                    sixMonthCheck.isSelected(),
                    referCheck.isSelected(),
                    urgentCheck.isSelected(),
                    laserCheck.isSelected(),
                    antiVegfCheck.isSelected(),
                    notes
            );

            if (recommendationsId == -1) {
                showAlert("Save Failed", "Recommendations were not saved.");
                return;
            }

            int patientId = 1; // temporary
            int doctorId = 1;  // temporary

            TestDAO testDAO = new TestDAO();
            int testId = testDAO.createTest(patientId, doctorId, imageId);

            if (testId == -1) {
                showAlert("Save Failed", "Test record was not saved.");
                return;
            }

            AppContext.getInstance().setCurrentTestId(testId);

            int reportId = reportDAO.createReport(
                    testId,
                    criticalityId,
                    findingsId,
                    recommendationsId,
                    evaluationId
            );

            if (reportId == -1) {
                showAlert("Save Failed", "Final report was not saved.");
                return;
            }

            showAlert(
                    "Report Generated",
                    "The diagnostic report has been finalized and saved."
            );

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save Error", "Something went wrong while saving the report.");
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
        try {
            // 1. Get the current stage
            // Use any FXML element you have (like notesArea or reportImageView) to get the scene
            Stage stage = (Stage) notesArea.getScene().getWindow();

            // 2. Locate the previous FXML
            // Based on your previous structure, it should be in /imageProcessing/
            var resource = getClass().getResource("/org/example/daibetes/modules/records/controller/doctorViewDiagnosis.fxml");

            if (resource == null) {
                System.err.println("/org/example/daibetes/modules/records/controller/doctorViewDiagnosis.fxml");
                // If the folder is named differently (e.g. all lowercase), update the string above
                return;
            }

            // 3. Load and set the scene
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            System.out.println("Returning to Image Processing screen.");

        } catch (Exception e) {
            System.err.println("Navigation Error (Back): " + e.getMessage());
            e.printStackTrace();
        }
    }



}