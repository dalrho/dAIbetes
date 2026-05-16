package recordsPatient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import register.sceneLoader;

public class startTest extends Application {
    @Override
    public void start(Stage stage) {

        Scene scene = sceneLoader.load(
                "recordsPatient",                  // folder inside resources
                "records-screen-patient.fxml",     // fxml file
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
