package imageUpload;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImageUploadApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("image-upload-view.fxml")
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Image Upload");
        stage.setScene(scene);

        stage.setWidth(900);
        stage.setHeight(600);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}