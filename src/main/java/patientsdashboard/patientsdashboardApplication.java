package patientsdashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import results.resultApplication;

public class patientsdashboardApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                resultApplication.class.getResource("/patientsdashboard/patients-dashboard.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("dAIbetes - Patients Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
