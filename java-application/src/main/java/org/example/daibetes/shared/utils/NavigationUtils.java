package org.example.daibetes.shared.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class NavigationUtils {

    public static void switchScene(Node sourceNode, String folder, String fxml, String title, String cssPath) {
        try {
            if (sourceNode == null) {
                throw new RuntimeException("ERROR: Source node is null. Cannot switch scene.");
            }

            String fullPath = "/" + folder + "/" + fxml;
            URL fxmlLocation = NavigationUtils.class.getResource(fullPath);

            if (fxmlLocation == null) {
                throw new RuntimeException("ERROR: Could not find FXML at " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Apply CSS dynamically
            if (cssPath != null) {
                URL css = NavigationUtils.class.getResource(cssPath);

                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                } else {
                    System.out.println("WARNING: CSS not found -> " + cssPath);
                }
            }

            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            ValidationUtils.showAlert("Navigation Error", "Unable to open page.");
        }
    }

    public static Scene load(String folder, String fxml, String cssPath) {
        try {
            String fullPath = "/" + folder + "/" + fxml;
            URL fxmlLocation = NavigationUtils.class.getResource(fullPath);

            if (fxmlLocation == null) {
                throw new RuntimeException("ERROR: Could not find FXML at " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Apply CSS dynamically
            if (cssPath != null) {
                URL css = NavigationUtils.class.getResource(cssPath);

                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                } else {
                    System.out.println("WARNING: CSS not found -> " + cssPath);
                }
            }

            return scene;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}