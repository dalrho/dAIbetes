package org.example.daibetes.modules.doctor.ui.popup.controller;

import org.example.daibetes.shared.ui.SceneLoader;
import org.example.daibetes.modules.patient.ui.upload.controller.ImageUploadController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
/**
 * PopDiagnosisController
 * Handles all interactions for the pop-up diagnosis screen
 *
 * Features:
 * - Upload up to 3 images from file system
 * - Capture images using device camera
 * - Image validation and storage
 * - User feedback through alerts and notifications
 */
public class PopDiagnosisController implements Initializable {

    @FXML private Button uploadImageBtn;
    @FXML private Button closeBtn;
    @FXML private Button openCameraBtn;
    @FXML private Button backBtn;

    private static final int MAX_IMAGES = 3;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private final List<File> uploadedImages = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupButtonHover(uploadImageBtn);

    }



    // =========================
    // UPLOAD IMAGES
    // =========================
    @FXML
    private void onUploadImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select up to 3 Images");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files", "*.jpg", "*.jpeg", "*.png", "*.bmp", "*.gif"
                    )
            );

            Stage stage = (Stage) uploadImageBtn.getScene().getWindow();
            List<File> files = fileChooser.showOpenMultipleDialog(stage);

            if (files == null || files.isEmpty()) return;

            processImages(files);

        } catch (Exception e) {
            showError("Upload Error", e.getMessage());
        }
    }

    // =========================
    // PROCESS IMAGES
    // =========================
    private void processImages(List<File> files) {

        StringBuilder errors = new StringBuilder();
        List<File> validImages = new ArrayList<>();

        for (File file : files) {

            if (validImages.size() >= 3) {
                errors.append("Maximum 3 images allowed.\n");
                break;
            }

            if (file.length() > MAX_FILE_SIZE) {
                errors.append(file.getName())
                        .append(" exceeds 10MB limit\n");
                continue;
            }

            if (!isValidImage(file)) {
                errors.append(file.getName())
                        .append(" is not a valid image\n");
                continue;
            }

            validImages.add(file);
        }

        if (!errors.isEmpty()) {
            showWarning("Some files were skipped", errors.toString());
        }

        if (!validImages.isEmpty()) {
            openUploadScreen(validImages);
        }
    }

    private void openUploadScreen(List<File> images) {

        try {

            // Load scene using your sceneLoader
            Scene scene = SceneLoader.load(
                    "org/example/daibetes/modules/patient/ui/upload",
                    "image-upload-view.fxml",
                    null // add CSS path here if you have one, e.g. "/imageupload/style.css"
            );

            if (scene == null) {
                showError("Error", "Failed to load upload screen");
                return;
            }

            // Get controller from FXMLLoader inside sceneLoader (IMPORTANT NOTE BELOW)
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/daibetes/modules/patient/ui/upload/image-upload-view.fxml")
            );
            Parent root = loader.load();

            ImageUploadController controller = loader.getController();

            // Pass images
            controller.setImages(images);

            Stage stage = new Stage();
            stage.setTitle("Uploaded Images");
            stage.setScene(new Scene(root));

            stage.show();

            // Close current window
            Stage current = (Stage) uploadImageBtn.getScene().getWindow();
            current.close();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", e.getMessage());
        }
    }

    // =========================
    // IMAGE VALIDATION
    // =========================
    private boolean isValidImage(File file) {
        try {
            BufferedImage img = ImageIO.read(file);
            return img != null;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================
    // UTILITIES
    // =========================
    private String listNames() {
        StringBuilder sb = new StringBuilder();
        for (File f : uploadedImages) {
            sb.append("• ").append(f.getName()).append("\n");
        }
        return sb.toString();
    }

    private void setupButtonHover(Button btn) {
        String style = btn.getStyle();

        btn.setOnMouseEntered(e ->
                btn.setStyle(style + "-fx-opacity: 0.8;"));

        btn.setOnMouseExited(e ->
                btn.setStyle(style));
    }


    // =========================
    // ALERTS
    // =========================
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showWarning(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    // =========================
    // GETTER
    // =========================
    public List<File> getUploadedImages() {
        return uploadedImages;
    }

    @FXML
    private void onOpenCamera() {
        try {
            // 1. Open the Camera Popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/daibetes/modules/detection/camera/ui/camera-popup.fxml"));
            Parent root = loader.load();

            Stage cameraStage = new Stage();
            cameraStage.setTitle("Capture Image");
            cameraStage.initModality(Modality.APPLICATION_MODAL); // Keeps it on top
            cameraStage.setScene(new Scene(root));

            // Wait for user to capture and close
            cameraStage.showAndWait();

            // 2. Retrieve the captured image from AppContext
            Image capturedImg = AppContext.getInstance().getSelectedImage();

            if (capturedImg != null) {
                // 3. Save Image to a temporary file so the next screen can handle it
                File tempFile = saveImageToTempFile(capturedImg);

                List<File> cameraList = new ArrayList<>();
                cameraList.add(tempFile);

                // 4. Open the gallery screen with this new "file"
                openUploadScreen(cameraList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Camera Error", "Could not initialize camera: " + e.getMessage());
        }
    }


    private File saveImageToTempFile(Image image) throws IOException {
        // Convert JavaFX Image back to BufferedImage
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        // Create a temp file
        File tempFile = File.createTempFile("captured_scan_", ".png");
        tempFile.deleteOnExit(); // Clean up when app closes

        // Write to file
        ImageIO.write(bImage, "png", tempFile);
        return tempFile;
    }

    @FXML
    private void onBack() {
        Stage stage = (Stage) backBtn.getScene().getWindow();

        stage.setScene(
                SceneLoader.load(
                        "org/example/daibetes/modules/doctor/ui/newdiagnosis",
                        "new-diagnosis-popup.fxml",
                        "/org/example/daibetes/styles/new-diagnosis.css"
                )
        );

        stage.setTitle("Select Patient");
        javafx.geometry.Rectangle2D screenBounds =
                javafx.stage.Screen.getPrimary().getVisualBounds();

        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);

        stage.show();
    }
}