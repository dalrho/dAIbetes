package org.example.daibetes.modules.doctor.ui.patients;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.daibetes.shared.ui.SceneLoader;

import java.io.IOException;

public class MyPatientsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(
                SceneLoader.load("/org/example/daibetes/modules/doctor/ui/patients", "my-patients-view.fxml", "/org/example/daibetes/styles/my-patient.css")
        );

        stage.setTitle("dAIbetes — Generate Report");
        stage.setMaximized(true);
        stage.show();
    }
}
