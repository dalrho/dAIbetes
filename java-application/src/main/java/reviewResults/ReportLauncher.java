package reviewResults;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import register.sceneLoader;
// THIS IS FOR THE PATIENT FXML VIEW DIAGNOSIS
public class ReportLauncher extends Application {
    @Override
    public void start(Stage stage) {

        Scene scene = sceneLoader.load(
                "reviewResults",                  // folder inside resources
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
