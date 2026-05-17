package org.example.daibetes.modules.splash.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import org.example.daibetes.shared.ui.SceneLoader;

public class splashApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Use your SceneLoader to keep logic consistent
        Scene scene = SceneLoader.load(
                "org/example/daibetes/modules/splash/controller",
                "splash-screen.fxml",
                "/org/example/daibetes/styles/splash.css"
        );

        stage.setTitle("dAIbetes");
        stage.setScene(scene);

        // Ensure the initial stage is maximized
//        stage.setFullScreenExitHint("");
//        stage.setFullScreen(true);
//        stage.show();
        // ADD THESE
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());

        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}