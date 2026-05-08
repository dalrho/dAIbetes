package imageUpload;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraController {
    @FXML private ImageView cameraView;
    private Webcam webcam;
    private final AtomicBoolean running = new AtomicBoolean(false);

    @FXML
    public void initialize() {
        startCamera();
    }

    private void startCamera() {
        webcam = Webcam.getDefault();
        if (webcam != null) {
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            webcam.open();
            running.set(true);

            Thread thread = new Thread(() -> {
                while (running.get()) {
                    BufferedImage img = webcam.getImage();
                    if (img != null) {
                        Image fxImage = SwingFXUtils.toFXImage(img, null);
                        Platform.runLater(() -> cameraView.setImage(fxImage));
                    }
                    try { Thread.sleep(30); } catch (InterruptedException e) { break; }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    private void handleCapture() {
        if (cameraView.getImage() != null) {
            // Save the captured frame to AppContext
            AppContext.getInstance().setSelectedImage(cameraView.getImage());
            handleClose();
        }
    }

    @FXML
    private void handleClose() {
        running.set(false);
        if (webcam != null) webcam.close();
        Stage stage = (Stage) cameraView.getScene().getWindow();
        stage.close();
    }
}