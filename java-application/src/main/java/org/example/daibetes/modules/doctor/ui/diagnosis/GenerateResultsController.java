package org.example.daibetes.modules.doctor.ui.diagnosis;

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
import org.example.daibetes.modules.ai.dto.AIResponseDTO;
import org.example.daibetes.modules.ai.service.AIInferenceService;
import org.example.daibetes.shared.models.Doctor;
import org.example.daibetes.shared.models.User;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.shared.service.IReportGenerationService;
import org.example.daibetes.shared.utils.ServiceRegistry;
import org.example.daibetes.shared.utils.AppExecutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.example.daibetes.shared.utils.ValidationUtils.showAlert;

public class GenerateResultsController {

    @FXML private ImageView reportImageView;
    @FXML private Label aiCriticalityLabel;
    @FXML private ComboBox<String> criticalityCombo;
    @FXML private TextArea doctorCriticalityArea;

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

    @FXML private Label aiConfidenceLabel;
    @FXML private Label aiExplanationLabel;
    @FXML private VBox aiResultBox;
    @FXML private ProgressIndicator aiLoader;

    private Image passedImage;

    @FXML
    public void initialize() {
        passedImage = AppContext.getInstance().getSelectedImage();

        if (passedImage != null) {
            reportImageView.setImage(passedImage);
        }

        String patientName = AppContext.getInstance().getSelectedRecordsPatientName();

        if (patientName == null || patientName.isBlank()) {
            patientName = AppContext.getInstance().getSelectedPatientName();
        }

        if (patientName != null && !patientName.isBlank()) {
            patientNameLabel.setText(patientName);
        }

        String notes = AppContext.getInstance().getDiagnosisNotes();

        if (notes != null && !notes.isBlank()) {
            diagnosisNotesLabel.setText(notes);
        }

        if (criticalityCombo != null) {
            criticalityCombo.getItems().setAll(
                    "Absent",
                    "Low",
                    "Moderate",
                    "High",
                    "Critical"
            );
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

                        aiCriticalityLabel.setText(
                                response.getPrediction().getPredicted_class()
                        );

                        double confidence =
                                response.getPrediction().getConfidence() * 100;

                        aiConfidenceLabel.setText(
                                String.format("%.2f%%", confidence)
                        );

                        aiExplanationLabel.setText(
                                response.getClinical_guidance()
                        );

                        aiResultBox.setStyle(
                                "-fx-background-color: #F0FDF4;" +
                                        "-fx-background-radius: 12;" +
                                        "-fx-padding: 20;" +
                                        "-fx-border-color: #BBF7D0;"
                        );
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
        if (group != null && group.getSelectedToggle() != null) {
            RadioButton rb = (RadioButton) group.getSelectedToggle();
            return rb.getText();
        }

        return "";
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
            showAlert("Missing Finding", "Please select a value for Cotton-Wool Spots.");
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

        int patientId = AppContext.getInstance().getSelectedRecordsPatientId();
        int doctorId = AppContext.getInstance().getSelectedRecordsDoctorId();
        String patientName = AppContext.getInstance().getSelectedRecordsPatientName();

        if (patientId == 0) {
            patientId = AppContext.getInstance().getSelectedPatientId();
        }

        if (doctorId == 0) {
            User currentUser = AppContext.getInstance().getCurrentUser();

            if (currentUser instanceof Doctor doctor) {
                doctorId = doctor.getDId();
            }
        }

        if (patientName == null || patientName.isBlank()) {
            patientName = AppContext.getInstance().getSelectedPatientName();
        }

        if (patientId == 0 || doctorId == 0 || patientName == null || patientName.isBlank()) {
            showAlert("Missing Data", "No selected patient or doctor found.");
            return;
        }

        try {
            File reportImageFile = imageViewToTempFile(reportImageView);

            if (reportImageFile == null || !reportImageFile.exists()) {
                showAlert("Image Error", "No valid report image file was created.");
                return;
            }

            IReportGenerationService reportService = ServiceRegistry.getInstance().get(IReportGenerationService.class);

            int reportId = reportService.generateAndPersistReport(
                    patientId,
                    doctorId,
                    reportImageFile,
                    criticality,
                    doctorReasoning,
                    microaneurysms,
                    hemorrhages,
                    exudates,
                    cottonWoolSpots,
                    macularEdemaFinding,
                    venousBeading,
                    irma,
                    neovascularization,
                    vitreousHemorrhage,
                    retinalDetachment,
                    drGrade,
                    dmeGrade,
                    annualFollowupCheck.isSelected(),
                    sixMonthCheck.isSelected(),
                    referCheck.isSelected(),
                    urgentCheck.isSelected(),
                    laserCheck.isSelected(),
                    antiVegfCheck.isSelected(),
                    notes
            );

            if (reportId == -1) {
                showAlert("Save Failed", "Final report was not saved.");
                return;
            }

            showAlert(
                    "Report Generated",
                    "The diagnostic report for " + patientName + " has been finalized and saved."
            );

            goBackToDoctorDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Save Error", "Something went wrong while saving the report.");
        }
    }

    private void goBackToRecords() {
        SceneLoader.switchScene(
                reportImageView,
                "org/example/daibetes/modules/doctor/ui/review",
                "records-screen.fxml",
                null,
                "Scan Records"
        );
    }

    private File imageViewToTempFile(ImageView imageView) {
        try {
            if (imageView.getImage() == null) {
                return null;
            }

            BufferedImage bufferedImage =
                    SwingFXUtils.fromFXImage(imageView.getImage(), null);

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
        SceneLoader.switchScene(
                notesArea,
                "org/example/daibetes/modules/detection/ui",
                "image-processing.fxml",
                null,
                "Image Processing"
        );

        Stage stage = (Stage) notesArea.getScene().getWindow();
    }
    private void goBackToDoctorDashboard() {
        SceneLoader.switchScene(
                notesArea,
                "org/example/daibetes/modules/doctor/dashboard",
                "doctor-dashboard.fxml",
                "Doctor Dashboard",
                "org/example/daibetes/styles/doctor-dashboard.css"
        );
    }
}