package patientsdashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import register.sceneLoader;
import results.resultApplication;

public class patientsdashboardApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Scene scene = sceneLoader.load(
                "patientsdashboard",
                "patients-dashboard.fxml",
                "/styles/doctorDashboard.css"
        );

        stage.setTitle("Patient Dashboard");
        stage.setScene(scene);
        stage.show();
    }

}
