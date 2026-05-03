package register;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RegisterApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/register/chooseRole.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Diabetes Detection System");
        stage.setScene(scene);
        stage.show();
    }
}