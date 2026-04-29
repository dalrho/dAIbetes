package org.example.daibetes.modules.detection.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.modules.detection.service.ScanAnalysisService;

public class ImageProcessingController {
    @FXML
    private ImageView rawImageView;
    @FXML private ImageView enhancedImageView;
    @FXML private ToggleButton grayscaleToggle;
    @FXML private Slider brightnessSlider;

    private final ScanAnalysisService analysisService = new ScanAnalysisService();
    private Image currentRawImage;

    @FXML
    public void initialize() {
        // Load the image passed from the Upload screen via AppContext
        currentRawImage = AppContext.getInstance().getSelectedImage();
        if (currentRawImage != null) {
            rawImageView.setImage(currentRawImage);
            updatePreview();
        }
    }

    @FXML
    private void updatePreview() {
        if (currentRawImage == null) return;

        // Controller ONLY gathers UI data and passes it to the Service
        Image result = analysisService.applyEnhancements(
                currentRawImage,
                grayscaleToggle.isSelected(),
                brightnessSlider.getValue(),
                0.0 // placeholder for contrast
        );

        enhancedImageView.setImage(result);
    }
}
