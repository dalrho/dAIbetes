package org.example.daibetes.modules.doctor.ui;

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

import org.example.daibetes.modules.ai.dto.AIResponseDTO;
import org.example.daibetes.modules.ai.service.AIInferenceService;

import java.io.File;

public class GenerateResultsController {

    @FXML private ImageView reportImageView;

    @FXML private Label aiCriticalityLabel;
    @FXML private Label aiBriefLabel;

    @FXML private ComboBox<String> criticalityCombo;

    @FXML private TextArea doctorCriticalityArea;
    @FXML private TextArea notesArea;

    @FXML private ToggleGroup tgMA;
    @FXML private ToggleGroup tgHem;
    @FXML private ToggleGroup tgExu;
    @FXML private ToggleGroup tgME;

    @FXML private ToggleGroup tgDR;
    @FXML private ToggleGroup tgDME;

    @FXML private CheckBox annualFollowupCheck;
    @FXML private CheckBox sixMonthCheck;
    @FXML private CheckBox referCheck;
    @FXML private CheckBox urgentCheck;
    @FXML private CheckBox laserCheck;
    @FXML private CheckBox antiVegfCheck;

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
        String macularEdema = getSelected(tgME);

        String drGrade = getSelected(tgDR);
        String dmeGrade = getSelected(tgDME);

        String criticality = criticalityCombo.getValue();
        String doctorReasoning = doctorCriticalityArea.getText();
        String notes = notesArea.getText();

        System.out.println("========== DIAGNOSTIC REPORT ==========");

        System.out.println("Microaneurysms: " + microaneurysms);
        System.out.println("Hemorrhages: " + hemorrhages);
        System.out.println("Hard Exudates: " + exudates);
        System.out.println("Macular Edema: " + macularEdema);

        System.out.println("DR Grade: " + drGrade);
        System.out.println("DME Grade: " + dmeGrade);

        System.out.println("Criticality: " + criticality);

        System.out.println("Doctor Analysis:");
        System.out.println(doctorReasoning);

        System.out.println("Recommendations:");

        if (annualFollowupCheck.isSelected()) {
            System.out.println("- Annual Follow-up");
        }

        if (sixMonthCheck.isSelected()) {
            System.out.println("- 6-month Follow-up");
        }

        if (referCheck.isSelected()) {
            System.out.println("- Refer to Specialist");
        }

        if (urgentCheck.isSelected()) {
            System.out.println("- Urgent Evaluation");
        }

        if (laserCheck.isSelected()) {
            System.out.println("- Laser Treatment");
        }

        if (antiVegfCheck.isSelected()) {
            System.out.println("- Anti-VEGF Therapy");
        }

        System.out.println("Final Notes:");
        System.out.println(notes);

        Alert success = new Alert(Alert.AlertType.INFORMATION);

        success.setTitle("Report Generated");
        success.setHeaderText(null);
        success.setContentText(
                "The diagnostic report has been finalized and saved."
        );

        success.showAndWait();
    }

    @FXML
    private void handleBack() {
        try {
            // 1. Get the current stage
            // Use any FXML element you have (like notesArea or reportImageView) to get the scene
            Stage stage = (Stage) notesArea.getScene().getWindow();

            // 2. Locate the previous FXML
            // Based on your previous structure, it should be in /imageProcessing/
            var resource = getClass().getResource("/imageProcessing/image-processing.fxml");

            if (resource == null) {
                System.err.println("ERROR: Could not find /imageProcessing/image-processing.fxml");
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