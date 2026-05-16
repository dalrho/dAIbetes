package org.example.daibetes.modules.doctor.ui.diagnosis;

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
import javafx.concurrent.Task;

import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.core.database.ReportDAO;
import org.example.daibetes.core.database.TestDAO;
import org.example.daibetes.modules.ai.dto.AIResponseDTO;
import org.example.daibetes.modules.ai.service.AIInferenceService;
import register.sceneLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

public class GenerateResultsController {

    @FXML private ImageView reportImageView;
    @FXML private Label aiCriticalityLabel;
    @FXML private ComboBox<String> criticalityCombo;
    @FXML private TextArea doctorCriticalityArea;

    // --- NEW PATIENT DISPLAY LABELS ---
    @FXML private Label patientNameLabel;
    @FXML private Label diagnosisNotesLabel;

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
        // 1. Pull the image that was just saved
        passedImage = AppContext.getInstance().getSelectedImage();

        if (passedImage != null) {
            reportImageView.setImage(passedImage);
        }

        // --- 2. NEW: PULL PATIENT CONTEXT FROM POPUP ---
        String patient = AppContext.getInstance().getSelectedPatientName();
        String notes = AppContext.getInstance().getDiagnosisNotes();

        if (patient != null) {
            patientNameLabel.setText(patient);
        }
        if (notes != null && !notes.isEmpty()) {
            diagnosisNotesLabel.setText(notes);
        }

        // 3. Populate ComboBox
        if (criticalityCombo != null) {
            criticalityCombo.getItems().addAll("Absent", "Low", "Moderate", "High", "Critical");
        }

        aiExplanationLabel.setText("This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsetsThis is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets" +
                "This is a fucking long ass text i bet you canno really go through all of this lololololololo test testetstestestsetafs test test etstestsetsetsets");
    }

    // ... handleAIDetection, handleSaveReport, and other methods remain unchanged ...
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
                        aiExplanationLabel.setText("No retinal image selected.");
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
                        aiResultBox.setStyle("-fx-background-color: #F0FDF4; -fx-background-radius: 12; -fx-padding: 20; -fx-border-color: #BBF7D0;");
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
        // ... (Same validation logic as before) ...
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
        String doctorReasoning = doctorCriticalityArea.getText() == null ? "" : doctorCriticalityArea.getText().trim();
        String notes = notesArea.getText() == null ? "" : notesArea.getText().trim();

        if (reportImageView.getImage() == null) { showAlert("Missing Image", "Please load an image."); return; }
        if (isBlank(microaneurysms)) { showAlert("Missing Finding", "Microaneurysms missing."); return; }
        // ... (other validation) ...

        try {
            ImageDAO imageDAO = new ImageDAO();
            ReportDAO reportDAO = new ReportDAO();
            File reportImageFile = imageViewToTempFile(reportImageView);
            if (reportImageFile == null) return;

            int imageId = imageDAO.createImage(reportImageFile, 1);
            int criticalityId = reportDAO.createCriticality(criticality, doctorReasoning);
            int findingsId = reportDAO.createPathologicalFindings(microaneurysms, hemorrhages, exudates, cottonWoolSpots, macularEdemaFinding, venousBeading, irma, neovascularization, vitreousHemorrhage, retinalDetachment);
            int evaluationId = reportDAO.createEvaluation(drGrade, dmeGrade);
            int recommendationsId = reportDAO.createRecommendations(annualFollowupCheck.isSelected(), sixMonthCheck.isSelected(), referCheck.isSelected(), urgentCheck.isSelected(), laserCheck.isSelected(), antiVegfCheck.isSelected(), notes);

            TestDAO testDAO = new TestDAO();
            int testId = testDAO.createTest(1, 1, imageId);
            reportDAO.createReport(testId, criticalityId, findingsId, recommendationsId, evaluationId);

            showAlert("Report Generated", "Saved successfully.");
            goToDoctorDashboard();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void goToDoctorDashboard() {
        Stage stage = (Stage) reportImageView.getScene().getWindow();
        stage.setScene(sceneLoader.load("doctorDashboard", "doctor-dashboard.fxml", "/styles/doctorDashboard.css"));
        stage.show();
    }

    private File imageViewToTempFile(ImageView imageView) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
            File tempFile = File.createTempFile("report_image_", ".png");
            tempFile.deleteOnExit();
            ImageIO.write(bufferedImage, "png", tempFile);
            return tempFile;
        } catch (Exception e) { return null; }
    }

    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }

    private boolean hasAtLeastOneRecommendation() {
        return annualFollowupCheck.isSelected() || sixMonthCheck.isSelected() || referCheck.isSelected() || urgentCheck.isSelected() || laserCheck.isSelected() || antiVegfCheck.isSelected();
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) notesArea.getScene().getWindow();
            var resource = getClass().getResource("/imageProcessing/image-processing.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (Exception e) { e.printStackTrace(); }
    }
}