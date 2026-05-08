package org.example.daibetes.modules.doctor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.daibetes.shared.utils.NavigationUtils;

import java.io.IOException;

public class GenerateResultsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = NavigationUtils.load(
                "generateReport",
                "generate-report.fxml",
                null
        );

        if (scene == null) {
            throw new RuntimeException("Failed to load initial screen.");
        }

        stage.setTitle("dAIbetes");
        stage.setScene(scene);
        stage.show();
    }
}
