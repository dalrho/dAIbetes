package org.example.daibetes.modules.patient.dashboard.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
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

        Scene scene = new Scene(root);

        primaryStage.setTitle("dAIbetes — Patient Dashboard");
        primaryStage.setScene(scene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setMaximized(true);

        primaryStage.show();
    }
}