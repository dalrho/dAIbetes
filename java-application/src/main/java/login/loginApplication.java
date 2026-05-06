package login;

import javafx.application.Application;
import javafx.stage.Stage;
import register.sceneLoader;

public class loginApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(
                sceneLoader.load(
                        "login",
                        "login-screen.fxml",
                        "/styles/splash.css"
                )
        );
        stage.setTitle("dAIbetes - Login");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}