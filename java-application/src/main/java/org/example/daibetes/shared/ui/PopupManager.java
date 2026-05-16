package org.example.daibetes.shared.ui;

import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopupManager {
    public static void open(String folder, String fxml, String css, String title) {
        try {
            Scene scene = SceneLoader.load(folder, fxml, css);
            if (scene == null) return;

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
