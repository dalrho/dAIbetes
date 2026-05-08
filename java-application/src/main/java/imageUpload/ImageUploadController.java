package imageUpload;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import register.PopupManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageUploadController {

    @FXML private ImageView img1;
    @FXML private ImageView img2;
    @FXML private ImageView img3;

    private final List<File> images = new ArrayList<>();

    // =========================
    // UPLOAD IMAGES
    // =========================
    @FXML
    private void onUpload() {

        if (images.size() >= 3) {
            showAlert("Limit reached", "You can only upload 3 images.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) img1.getScene().getWindow();
        List<File> files = fc.showOpenMultipleDialog(stage);

        if (files == null) return;

        for (File f : files) {
            if (images.size() >= 3) break;
            images.add(f);
        }

        refresh();
    }

    // =========================
    // REMOVE IMAGE
    // =========================
    @FXML private void remove1() { removeAt(0); }
    @FXML private void remove2() { removeAt(1); }
    @FXML private void remove3() { removeAt(2); }

    private void removeAt(int index) {
        if (index < images.size()) {
            images.remove(index);
            refresh();
        }
    }

    // =========================
    // REFRESH UI
    // =========================
    private void refresh() {

        img1.setImage(null);
        img2.setImage(null);
        img3.setImage(null);

        if (images.size() > 0)
            img1.setImage(new Image(images.get(0).toURI().toString()));

        if (images.size() > 1)
            img2.setImage(new Image(images.get(1).toURI().toString()));

        if (images.size() > 2)
            img3.setImage(new Image(images.get(2).toURI().toString()));
    }

    // =========================
    // NEXT BUTTON (PLACEHOLDER)
    // =========================
    @FXML
    private void onNext() {
        showAlert("Next", "Next button clicked (to be implemented).");
    }

    // =========================
    // back button
    // =========================
    @FXML
    private void onBack() {
        try {

            // open previous popup again using the manager
            PopupManager.open(
                    "popdiagnosis",
                    "popdiagnosis-screen.fxml",
                    null,
                    "New Diagnosis"
            );

            // close current window (ImageUpload)
            Stage stage = (Stage) img1.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // =========================
    // ALERT
    // =========================
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
    public void setImages(List<File> files) {

        images.clear();

        for (File f : files) {
            if (images.size() >= 3) break;
            images.add(f);
        }

        refresh();
    }

    public List<File> getImages() {
        return images;
    }
}