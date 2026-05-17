package org.example.daibetes.modules.detection.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.modules.detection.service.ScanAnalysisService;
import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.shared.utils.AppExecutor;

public class ImageProcessingController {
    @FXML private ImageView rawImageView, enhancedImageView;
    @FXML private Slider brightnessSlider, claheSlider, sharpSlider, denoiseSlider, zoomSlider;
    @FXML private ComboBox<String> resizeCombo;
    @FXML private ToggleButton grayscaleToggle;

    private final ScanAnalysisService analysisService = new ScanAnalysisService();
    private Image currentRawImage;

    private Task<Image> currentProcessingTask;
    private javafx.animation.Timeline debounceTimeline;

    @FXML
    public void initialize() {
        currentRawImage = AppContext.getInstance().getSelectedImage();

        resizeCombo.getItems().setAll("Original", "400px", "600px", "800px");
        resizeCombo.setValue("Original");

        if (enhancedImageView != null && rawImageView != null) {
            enhancedImageView.scaleXProperty().bind(zoomSlider.valueProperty());
            enhancedImageView.scaleYProperty().bind(zoomSlider.valueProperty());
            rawImageView.scaleXProperty().bind(zoomSlider.valueProperty());
            rawImageView.scaleYProperty().bind(zoomSlider.valueProperty());
        }

        if (currentRawImage != null) {
            rawImageView.setImage(currentRawImage);
            updatePreview();
        }
    }

    @FXML
    private void updatePreview() {
        if (currentRawImage == null) return;

        if (currentProcessingTask != null && currentProcessingTask.isRunning()) {
            currentProcessingTask.cancel();
        }

        if (debounceTimeline != null) {
            debounceTimeline.stop();
        }

        debounceTimeline = new javafx.animation.Timeline(new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(150),
            event -> startProcessingTask()
        ));
        debounceTimeline.play();
    }

    private void startProcessingTask() {
        boolean isGray = grayscaleToggle.isSelected();
        double bright = brightnessSlider.getValue();
        double clahe = claheSlider.getValue();
        double sharp = sharpSlider.getValue();
        double denoise = denoiseSlider.getValue();
        int targetWidth = 0;
        String resizeVal = resizeCombo.getValue();
        if (resizeVal != null && !resizeVal.equals("Original")) {
            targetWidth = Integer.parseInt(resizeVal.replace("px", ""));
        }

        final int finalWidth = targetWidth;

        currentProcessingTask = new Task<>() {
            @Override
            protected Image call() throws Exception {
                // This heavy lifting happens on a separate thread in the executor pool
                return analysisService.applyEnhancements(
                        currentRawImage, isGray, bright, clahe, sharp, denoise, finalWidth
                );
            }
        };

        currentProcessingTask.setOnSucceeded(event -> {
            enhancedImageView.setImage(currentProcessingTask.getValue());
        });

        currentProcessingTask.setOnFailed(event -> {
            Throwable e = currentProcessingTask.getException();
            if (e != null) e.printStackTrace();
        });

        AppExecutor.get().submit(currentProcessingTask);
    }

    @FXML
    private void handleReset() {
        brightnessSlider.setValue(0);
        claheSlider.setValue(1.0);
        sharpSlider.setValue(0);
        denoiseSlider.setValue(0);
        zoomSlider.setValue(1.0);
        grayscaleToggle.setSelected(false);
        resizeCombo.setValue("Original");
        updatePreview();
    }

    @FXML
    private void handleBackToGallery() {
        try {
            SceneLoader.switchScene(
                    enhancedImageView, // source node (same window reference)
                    "org/example/daibetes/modules/patient/ui/upload",
                    "image-upload-view.fxml",
                    "Image Gallery",
                    "/org/example/daibetes/styles/new-diagnosis.css"
            );
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
    @FXML
    private void handleGenerateReport() {
        Image saveImage = rawImageView.getImage();

        if (saveImage == null) {
            System.err.println("No image available to generate a report.");
            return;
        }

        AppContext.getInstance().setSelectedImage(saveImage);

        try {
            Stage currentStage = (Stage) enhancedImageView.getScene().getWindow();

            for (Window window : Window.getWindows()) {
                if (window instanceof Stage stage && stage != currentStage) {
                    stage.close();
                }
            }

            SceneLoader.switchScene(
                    enhancedImageView,
                    "org/example/daibetes/modules/doctor/ui/diagnosis",
                    "generate-report.fxml",
                    "Generate Report",
                    null

            );

            currentStage.setMaximized(true);

        } catch (Exception e) {
            System.err.println("Navigation Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleAIDetection() {
        System.out.println("Processing AI Detection on the enhanced image...");
    }
}