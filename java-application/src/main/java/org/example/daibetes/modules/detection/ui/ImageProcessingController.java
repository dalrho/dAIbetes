package org.example.daibetes.modules.detection.ui;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.modules.detection.service.ScanAnalysisService;

public class ImageProcessingController {
    @FXML private ImageView rawImageView, enhancedImageView;
    @FXML private Slider brightnessSlider, claheSlider, sharpSlider, denoiseSlider, zoomSlider;
    @FXML private ComboBox<String> resizeCombo;
    @FXML private ToggleButton grayscaleToggle;

    private final ScanAnalysisService analysisService = new ScanAnalysisService();
    private Image currentRawImage;

    private Task<Image> currentProcessingTask;

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
                // This heavy lifting happens on a separate core
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

        Thread thread = new Thread(currentProcessingTask);
        thread.setDaemon(true);
        thread.start();
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
            Stage stage = (Stage) enhancedImageView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/imageUpload/image-upload-view.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
    @FXML
    private void handleGenerateReport() {
        Image finalProcessedImage = enhancedImageView.getImage();

        if (finalProcessedImage == null) {
            System.err.println("No image available to generate a report.");
            return;
        }

        // Save image to AppContext
        AppContext.getInstance().setSelectedImage(finalProcessedImage);

        try {
            Stage stage = (Stage) enhancedImageView.getScene().getWindow();

            // 1. Try to find the resource
            var resource = getClass().getResource("/generateReport/generate-report.fxml");

            // 2. Debug check: if this is null, the path is wrong
            if (resource == null) {
                System.err.println("ERROR: Could not find /generateReport/generate-report.fxml");
                System.err.println("Check if the folder is named 'generateReport' or 'generatereport'");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.centerOnScreen();

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