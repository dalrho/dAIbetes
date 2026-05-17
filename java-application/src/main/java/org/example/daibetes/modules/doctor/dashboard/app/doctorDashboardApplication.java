package org.example.daibetes.modules.doctor.dashboard.app;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

public class doctorDashboardApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/doctor/dashboard",
                "doctor-dashboard.fxml",
                "/org/example/daibetes/styles/doctor-dashboard.css"
        );

        stage.setTitle("Doctor Dashboard");
        // MUST SET SCENE FIRST
        stage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setMaximized(true);
        stage.show();
    }
}
