package org.example.daibetes.shared.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class SceneLoader {

    /**
     * Loads an FXML and returns its Scene.
     * Used internally by the register package.
     */
    public static Scene load(String folder, String fxml, String cssPath) {
        try {
            String fullPath = "/" + folder + "/" + fxml;
            URL xmlLocation = SceneLoader.class.getResource(fullPath);

            if (xmlLocation == null) {
                throw new RuntimeException("ERROR: Could not find FXML at " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(xmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            if (cssPath != null) {
                URL css = SceneLoader.class.getResource(cssPath);
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

    /**
     * Loads an FXML and switches the current stage to the new scene.
     * Used project-wide as the single navigation utility.
     *
     * @param sourceNode any Node on the current stage (used to get the Stage)
     * @param folder     resource folder name (e.g. "org/example/daibetes/modules/auth/login", "org/example/daibetes/modules/auth/register", "org/example/daibetes/modules/patient/dashboard")
     * @param fxml       FXML filename (e.g. "login-screen.fxml")
     * @param title      window title
     * @param cssPath    optional CSS resource path, null if none
     */
    public static void switchScene(Node sourceNode, String folder,
                                   String fxml, String title, String cssPath) {
        try {
            if (sourceNode == null) {
                throw new RuntimeException("ERROR: Source node is null. Cannot switch scene.");
            }

            String fullPath = "/" + folder + "/" + fxml;
            URL fxmlLocation = SceneLoader.class.getResource(fullPath);

            if (fxmlLocation == null) {
                throw new RuntimeException("ERROR: Could not find FXML at " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            if (cssPath != null) {
                URL css = SceneLoader.class.getResource(cssPath);
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
        }
    }

    /**
     * Loads an FXML, switches the scene, and returns the controller instance.
     * Used when the caller needs to pass data to the next controller
     * (e.g. login → dashboard passing the authenticated user).
     *
     * @return the controller instance, or null on failure
     */
    public static <T> T switchSceneWithController(Node sourceNode, String folder,
                                                  String fxml, String title, String cssPath) {
        try {
            if (sourceNode == null) {
                throw new RuntimeException("ERROR: Source node is null. Cannot switch scene.");
            }

            String fullPath = "/" + folder + "/" + fxml;
            URL fxmlLocation = SceneLoader.class.getResource(fullPath);

            if (fxmlLocation == null) {
                throw new RuntimeException("ERROR: Could not find FXML at " + fullPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);

            if (cssPath != null) {
                URL css = SceneLoader.class.getResource(cssPath);
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

            return loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Image loadImage(String path) {
        URL url = SceneLoader.class.getResource(path);
        if (url == null) return null;
        return new Image(url.toExternalForm());
    }
}