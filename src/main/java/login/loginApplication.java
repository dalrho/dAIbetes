package login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import results.resultApplication;

public class loginApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    resultApplication.class.getResource("/login/login-screen.fxml")
            );

            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("dAIbetes - Login");
            stage.setScene(scene);
            stage.show();
        }

        public static void main(String[] args) {
            launch();
        }
}
