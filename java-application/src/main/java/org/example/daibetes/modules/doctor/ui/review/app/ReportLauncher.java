package org.example.daibetes.modules.doctor.ui.review.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;
// THIS IS FOR THE PATIENT FXML VIEW DIAGNOSIS
public class ReportLauncher extends Application {
    @Override
    public void start(Stage stage) {

        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/doctor/ui/review",                  // folder inside resources
                "review_results.fxml",     // fxml file
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
