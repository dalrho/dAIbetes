package splashscreen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import results.resultApplication;


public class splashApplication extends Application {
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                resultApplication.class.getResource("/splashscreen/splash-screen.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("dAIbetes - Splash");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
