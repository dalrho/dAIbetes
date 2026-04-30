package org.example.daibetes.modules.detection.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.daibetes.HelloApplication;
import org.example.daibetes.app.AppContext;

import java.io.IOException;

public class ImageProcessorTestApp extends Application {
    public void start(Stage stage) throws IOException {
        Image testImage = new Image(
                ImageProcessorTestApp.class
                        .getResource("/test/sample.jpg")
                        .toExternalForm()
        );

        AppContext.getInstance().setSelectedImage(testImage);

        FXMLLoader fxmlLoader = new FXMLLoader(
                ImageProcessorTestApp.class.getResource("/imageProcessing/image-processing.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
