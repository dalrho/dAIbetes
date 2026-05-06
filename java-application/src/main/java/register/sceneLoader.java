package register;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.net.URL;

public class sceneLoader {

    // Now accepts CSS path as a parameter
    public static Scene load(String folder, String fxml, String cssPath) {
        try {
            String fullPath = "/" + folder + "/" + fxml;
            URL xmlLocation = sceneLoader.class.getResource(fullPath);

            if (xmlLocation == null) {
                throw new RuntimeException("ERROR: Could not find FXML at " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(xmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);

            // Apply CSS dynamically
            if (cssPath != null) {
                URL css = sceneLoader.class.getResource(cssPath);
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

    public static Image loadImage(String path) {
        URL url = sceneLoader.class.getResource(path);
        if (url == null) return null;
        return new Image(url.toExternalForm());
    }
}