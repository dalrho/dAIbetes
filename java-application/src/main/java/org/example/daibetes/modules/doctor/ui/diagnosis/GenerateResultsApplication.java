package org.example.daibetes.modules.doctor.ui.diagnosis;

import javafx.application.Application;
import javafx.stage.Stage;
import register.sceneLoader;

import java.io.IOException;

public class GenerateResultsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(
                sceneLoader.load("generateReport", "generate-report.fxml", null)
        );

        stage.setTitle("dAIbetes — Generate Report");
        stage.show();
    }
}
