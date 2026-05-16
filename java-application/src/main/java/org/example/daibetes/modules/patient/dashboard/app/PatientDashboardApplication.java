package org.example.daibetes.modules.patient.dashboard.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX Application entry point for the Patient Dashboard.
 * In production this screen is reached via loginController after login —
 * this Application class exists for standalone testing.
 */
public class PatientDashboardApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/daibetes/modules/patient/dashboard/patients-dashboard.fxml")
        );
        Parent root = loader.load();

        primaryStage.setTitle("dAIbetes — Patient Dashboard");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}