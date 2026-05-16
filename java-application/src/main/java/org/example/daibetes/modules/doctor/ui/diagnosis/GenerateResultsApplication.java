package org.example.daibetes.modules.doctor.ui.diagnosis;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

import java.io.IOException;

public class GenerateResultsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(
                SceneLoader.load("org/example/daibetes/modules/doctor/ui/diagnosis", "generate-report.fxml", null)
        );

        stage.setTitle("dAIbetes — Generate Report");
        stage.show();
    }
}
