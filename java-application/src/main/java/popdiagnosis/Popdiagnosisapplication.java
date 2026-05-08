package popdiagnosis;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * PopDiagnosisApplication
 * Main entry point for the Pop-up Diagnosis Screen application
 *
 * This application allows users to start a new diagnosis by either:
 * - Uploading existing images (2-3 photos)
 * - Capturing images using the device camera
 */
public class Popdiagnosisapplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Popdiagnosislauncher launcher = new Popdiagnosislauncher();
        launcher.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
