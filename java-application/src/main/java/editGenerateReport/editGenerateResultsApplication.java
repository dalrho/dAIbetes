package editGenerateReport;

import javafx.application.Application;
import javafx.stage.Stage;
import register.sceneLoader;

import java.io.IOException;

public class editGenerateResultsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        stage.setScene(
                sceneLoader.load("editGenerateReport", "edit-generate-report.fxml", null)
        );

        stage.setTitle("dAIbetes — Generate Report");
        stage.show();
    }
}
