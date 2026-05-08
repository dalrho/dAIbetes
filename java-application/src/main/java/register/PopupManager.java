package register;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopupManager {

    public static Stage open(String folder, String fxml, String cssPath, String title) {
        try {

            Scene scene = sceneLoader.load(folder, fxml, cssPath);

            if (scene == null) {
                System.out.println("Popup failed to load: " + folder + "/" + fxml);
                return null;
            }

            Stage stage = new Stage();

            stage.setTitle(title);

            // 🔥 THIS IS THE KEY (removes gray background)
            stage.initStyle(StageStyle.TRANSPARENT);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // 🔥 REQUIRED for transparent Stage
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);

            stage.show();

            return stage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}