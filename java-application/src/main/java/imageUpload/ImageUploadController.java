package imageUpload;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.core.database.ImageDAO;
import org.example.daibetes.modules.detection.ui.ImageProcessingController;
import register.PopupManager;

import java.io.File;
import java.util.List;

public class ImageUploadController {

    @FXML private ImageView img1, img2, img3;
    @FXML private VBox box1, box2, box3;

    // We no longer keep a local list here, we use AppContext
    private List<File> images;
    private int selectedImageIndex = 0;

    @FXML
    public void initialize() {
        // LOAD images from AppContext so they persist when coming back
        this.images = AppContext.getInstance().getGalleryFiles();
        refresh();
    }

    @FXML private void onSelect1() { updateSelection(0); }
    @FXML private void onSelect2() { updateSelection(1); }
    @FXML private void onSelect3() { updateSelection(2); }

    private void updateSelection(int index) {
        if (index < images.size()) {
            selectedImageIndex = index;
            refreshSelectionStyles();
        }
    }

    private void refreshSelectionStyles() {
        String base = "-fx-padding: 5; -fx-border-color: transparent;";
        box1.setStyle(base); box2.setStyle(base); box3.setStyle(base);

        String active = "-fx-padding: 5; -fx-border-color: #2ecc71; -fx-border-width: 3; -fx-border-radius: 5; -fx-background-color: #fafffa;";

        if (selectedImageIndex == 0 && !images.isEmpty()) box1.setStyle(active);
        else if (selectedImageIndex == 1 && images.size() > 1) box2.setStyle(active);
        else if (selectedImageIndex == 2 && images.size() > 2) box3.setStyle(active);
    }

    @FXML
    private void onUpload() {
        if (images.size() >= 3) {
            showAlert("Limit reached", "You can only upload 3 images.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) img1.getScene().getWindow();
        List<File> files = fc.showOpenMultipleDialog(stage);

        if (files != null) {
            for (File f : files) {
                if (images.size() >= 3) break;
                images.add(f);
            }
            // Save to context immediately
            AppContext.getInstance().setGalleryFiles(images);
            refresh();
        }
    }

    @FXML private void remove1() { removeAt(0); }
    @FXML private void remove2() { removeAt(1); }
    @FXML private void remove3() { removeAt(2); }

    private void removeAt(int index) {
        if (index < images.size()) {
            images.remove(index);
            AppContext.getInstance().setGalleryFiles(images);
            if (selectedImageIndex >= images.size()) {
                selectedImageIndex = Math.max(0, images.size() - 1);
            }
            refresh();
        }
    }

    private void refresh() {
        img1.setImage(null); img2.setImage(null); img3.setImage(null);

        if (images.size() > 0) img1.setImage(new Image(images.get(0).toURI().toString()));
        if (images.size() > 1) img2.setImage(new Image(images.get(1).toURI().toString()));
        if (images.size() > 2) img3.setImage(new Image(images.get(2).toURI().toString()));

        refreshSelectionStyles();
    }

    @FXML
    private void onNext() {
        if (images.isEmpty()) {
            showAlert("No Image", "Please upload at least one image.");
            return;
        }

        try {
            // Get selected image file
            File selectedFile = images.get(selectedImageIndex);

            // Save selected image to tblimage
            ImageDAO imageDAO = new ImageDAO();

            // image_type_id does not matter yet, so use 1 temporarily
            int imageId = imageDAO.createImage(selectedFile, 1);

            if (imageId == -1) {
                showAlert("Upload Failed", "Image was not saved to the database.");
                return;
            }

            // Convert selected file to JavaFX Image for display/use in next screen
            Image runtimeSelectedImage = new Image(selectedFile.toURI().toString());

            // Store selected image in AppContext
            AppContext.getInstance().setSelectedImage(runtimeSelectedImage);

            // Optional but recommended:
            // Store img_id too so you can use it later in tbltests, tblfilteredscans, etc.
            AppContext.getInstance().setSelectedImageId(imageId);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/imageProcessing/image-processing.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) img1.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Something went wrong while processing the image.");
        }
    }
    @FXML
    private void onBack() {
        try {
            PopupManager.open("popdiagnosis", "popdiagnosis-screen.fxml", null, "New Diagnosis");
            Stage stage = (Stage) img1.getScene().getWindow();
            stage.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Keep existing getters/setters for external calls if needed
    public void setImages(List<File> files) {
        this.images = files;
        AppContext.getInstance().setGalleryFiles(files);
        refresh();
    }
    public List<File> getImages() { return images; }
}