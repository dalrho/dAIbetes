package popdiagnosis;


import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import register.sceneLoader;

/**
 * PopDiagnosisLauncher
 * Responsible for loading the FXML file and configuring the dialog stage
 *
 * Features:
 * - Loads popdiagnosis-screen.fxml
 * - Configures stage as a modal dialog
 * - Handles transparent background with semi-transparent overlay
 */
public class Popdiagnosislauncher {

    public void start(Stage ownerStage) {

        try {
            Scene scene = sceneLoader.load(
                    "popdiagnosis",
                    "popdiagnosis-screen.fxml",
                    null
            );

            Stage stage = new Stage();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle("New Diagnosis");

            // 🔥 IMPORTANT: match other screens automatically
            stage.setScene(scene);

            // ❌ REMOVE hardcoded size
            stage.sizeToScene();

            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Popdiagnosisapplication.main(args);
    }
}