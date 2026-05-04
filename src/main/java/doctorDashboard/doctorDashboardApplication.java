package doctorDashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.diabetes.HelloApplication;
import register.sceneLoader;

import java.io.IOException;

public class doctorDashboardApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Scene scene = sceneLoader.load(
                "doctorDashboard",
                "doctor-dashboard.fxml",
                "/styles/doctorDashboard.css"
        );

        stage.setTitle("Doctor Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
