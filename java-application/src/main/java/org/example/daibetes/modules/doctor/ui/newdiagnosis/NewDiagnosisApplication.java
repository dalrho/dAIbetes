package org.example.daibetes.modules.doctor.ui.newdiagnosis;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

import static javafx.application.Application.launch;

public class NewDiagnosisApplication extends Application {
    public void start(Stage stage) throws Exception {
        // For this demo, we load the popup directly
        stage.setScene(
                SceneLoader.load("org/example/daibetes/modules/doctor/ui/newdiagnosis",
                        "new-diagnosis-popup.fxml",
                        "/org/example/daibetes/styles/new-diagnosis.css")
        );

        stage.setTitle("dAIbetes — Generate Report");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
