package records;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import register.sceneLoader;

public class RecordsLauncher extends Application {
    //DOCTOR LAUNCHER CONTAINS EDIT DAIGNOSIS
    @Override
    public void start(Stage stage) {

        Scene scene = sceneLoader.load(
                "records",                  // folder inside resources
                "records-screen.fxml",     // fxml file
                null                       // css path (add later if needed)
        );

        if (scene == null) {
            System.out.println("Failed to load Records Screen");
            return;
        }

        stage.setTitle("Scan Records Dashboard");
        stage.setScene(scene);
        // 🔥 FORCE CONSISTENT SIZE
        stage.setWidth(900);
        stage.setHeight(600);

        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaxWidth(900);
        stage.setMaxHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}