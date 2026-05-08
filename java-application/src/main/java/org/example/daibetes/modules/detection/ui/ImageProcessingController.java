package org.example.daibetes.modules.detection.ui;

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

    @FXML
    public void initialize() {
        currentRawImage = AppContext.getInstance().getSelectedImage();

        // Populate Resize Options
        resizeCombo.getItems().setAll("Original", "400px", "600px", "800px");
        resizeCombo.setValue("Original");

        // Sync Zoom Logic: Bind image scale to zoom slider
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
            // Path must match your actual Gallery FXML location
            Stage stage = (Stage) enhancedImageView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/daibetes/modules/detection/ui/GalleryView.fxml"));
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
    @FXML
    private void handleGenerateReport() {
        // Logic remains the same, just renamed for the new button purpose
        System.out.println("Generating Diagnostic Report from enhanced image...");
        // Implement report generation or export logic here
    }


    @FXML
    private void handleAIDetection() {
        // Here you would pass 'enhancedImageView.getImage()' to your model
        System.out.println("Processing AI Detection on the enhanced image...");
    }

    @FXML
    private void updatePreview() {
        if (currentRawImage == null) return;

        int targetWidth = 0;
        String resizeVal = resizeCombo.getValue();
        if (resizeVal != null && !resizeVal.equals("Original")) {
            targetWidth = Integer.parseInt(resizeVal.replace("px", ""));
        }

        Image result = analysisService.applyEnhancements(
                currentRawImage,
                grayscaleToggle.isSelected(),
                brightnessSlider.getValue(),
                claheSlider.getValue(),
                sharpSlider.getValue(),
                denoiseSlider.getValue(),
                targetWidth
        );

        enhancedImageView.setImage(result);
    }
}