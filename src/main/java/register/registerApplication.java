package register;

import javafx.application.Application;
import javafx.stage.Stage;

public class registerApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(
                sceneLoader.load(
                        "register",
                        "register-screen.fxml",
                        "/styles/splash.css"
                )
        );
        stage.setTitle("dAIbetes - Register");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}