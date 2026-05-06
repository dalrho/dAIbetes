package records;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;


public class RecordsApplication extends Application {



    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Objects.requireNonNull(RecordsApplication.class.getResource("/records/records.fxml"))
            );
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root, 1400, 900);

            primaryStage.setTitle("dAIbetes - Records Manager");
            primaryStage.setScene(scene);
            primaryStage.setWidth(1400);
            primaryStage.setHeight(900);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load FXML resource: " + "/records/records.fxml");
            e.printStackTrace();
            throw e;
        } catch (NullPointerException e) {
            System.err.println("FXML resource not found: " + "/records/records.fxml");
            e.printStackTrace();
            throw new IOException("FXML resource not found", e);
        }
    }
}