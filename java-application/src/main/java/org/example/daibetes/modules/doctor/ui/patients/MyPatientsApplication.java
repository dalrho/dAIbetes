package org.example.daibetes.modules.doctor.ui.patients;

import javafx.application.Application;
import javafx.stage.Stage;
import register.sceneLoader;

import java.io.IOException;

public class MyPatientsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(
                sceneLoader.load("myPatients", "my-patients-view.fxml",    "/styles/myPatient.css" )
        );

        stage.setTitle("dAIbetes — Generate Report");
        stage.show();
    }
}
