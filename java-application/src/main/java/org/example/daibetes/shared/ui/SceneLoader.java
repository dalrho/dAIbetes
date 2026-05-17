package org.example.daibetes.shared.ui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;

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
                throw new RuntimeException(
                        "ERROR: Could not find FXML at " + fullPath
                );
            }

            FXMLLoader loader = new FXMLLoader(xmlLocation);
            Parent root = loader.load();

            // INITIAL FADE IN
            root.setOpacity(0.96);
            FadeTransition fade = new FadeTransition(
                    Duration.millis(120),
                    root
            );

            fade.setFromValue(0.96);
            fade.setToValue(1);
            fade.play();

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.BLACK);
            if (cssPath != null) {

                URL css = SceneLoader.class.getResource(cssPath);

                if (css != null) {
                    scene.getStylesheets().add(css.toExternalForm());
                } else {
                    System.out.println(
                            "WARNING: CSS not found -> " + cssPath
                    );
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
     */
    public static void switchScene(
            Node sourceNode,
            String folder,
            String fxml,
            String title,
            String cssPath
    ) {

        try {

            if (sourceNode == null) {

                throw new RuntimeException(
                        "ERROR: Source node is null. Cannot switch scene."
                );
            }

            String fullPath = "/" + folder + "/" + fxml;

            URL fxmlLocation =
                    SceneLoader.class.getResource(fullPath);

            if (fxmlLocation == null) {

                throw new RuntimeException(
                        "ERROR: Could not find FXML at " + fullPath
                );
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);

            Parent root = loader.load();

            // START INVISIBLE
            root.setOpacity(0);

            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.BLACK);
            if (cssPath != null) {

                URL css = SceneLoader.class.getResource(cssPath);

                if (css != null) {

                    scene.getStylesheets().add(
                            css.toExternalForm()
                    );

                } else {

                    System.out.println(
                            "WARNING: CSS not found -> " + cssPath
                    );
                }
            }

            Stage stage =
                    (Stage) sourceNode
                            .getScene()
                            .getWindow();

            stage.setTitle(title);

            stage.setScene(scene);

            // KEEP FULLSCREEN / MAXIMIZED
            stage.setMaximized(true);

            stage.show();

            // SMOOTH FADE TRANSITION
            FadeTransition fade = new FadeTransition(
                    Duration.millis(500),
                    root
            );

            fade.setFromValue(0);
            fade.setToValue(1);

            fade.play();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Loads an FXML, switches the scene,
     * and returns the controller instance.
     */
    public static <T> T switchSceneWithController(
            Node sourceNode,
            String folder,
            String fxml,
            String title,
            String cssPath
    ) {

        try {

            if (sourceNode == null) {

                throw new RuntimeException(
                        "ERROR: Source node is null. Cannot switch scene."
                );
            }

            String fullPath = "/" + folder + "/" + fxml;

            URL fxmlLocation =
                    SceneLoader.class.getResource(fullPath);

            if (fxmlLocation == null) {

                throw new RuntimeException(
                        "ERROR: Could not find FXML at " + fullPath
                );
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);

            Parent root = loader.load();

            // START INVISIBLE
            root.setOpacity(0);

            Scene scene = new Scene(root);

            if (cssPath != null) {

                URL css = SceneLoader.class.getResource(cssPath);

                if (css != null) {

                    scene.getStylesheets().add(
                            css.toExternalForm()
                    );

                } else {

                    System.out.println(
                            "WARNING: CSS not found -> " + cssPath
                    );
                }
            }

            Stage stage =
                    (Stage) sourceNode
                            .getScene()
                            .getWindow();

            stage.setTitle(title);

            stage.setScene(scene);

            // KEEP FULLSCREEN / MAXIMIZED
            stage.setMaximized(true);

            stage.show();

            // SMOOTH FADE
            FadeTransition fade = new FadeTransition(
                    Duration.millis(500),
                    root
            );

            fade.setFromValue(0);
            fade.setToValue(1);

            fade.play();

            return loader.getController();

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    public static Image loadImage(String path) {

        URL url = SceneLoader.class.getResource(path);

        if (url == null) {
            return null;
        }

        return new Image(url.toExternalForm());
    }
}