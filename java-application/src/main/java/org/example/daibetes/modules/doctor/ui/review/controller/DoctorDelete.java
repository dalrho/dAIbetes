package org.example.daibetes.modules.doctor.ui.review.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

public class DoctorDelete extends Application {
    @Override
    public void start(Stage stage) {

        Scene scene = SceneLoader.load(
                "DoctorViewDiagnosis",                  // folder inside resources
                "doctorViewDiagnosis.fxml",     // fxml file
                null                       // css path (add later if needed)
        );

        if (scene == null) {
            System.out.println("Failed to load Records Screen");
            return;
        }

        stage.setTitle("Scan Records Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
