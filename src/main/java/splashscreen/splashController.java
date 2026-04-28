package splashscreen;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class splashController {

    @FXML
    private VBox rootContainer;

    // Runs automatically when FXML loads
    @FXML
    public void initialize() {
        playFadeIn();
    }

    private void playFadeIn() {
        rootContainer.setOpacity(0);

        FadeTransition fade = new FadeTransition(Duration.millis(1200), rootContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setCycleCount(1);
        fade.setAutoReverse(false);
        fade.play();
    }

    public void handleStart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("results.fxml")
            );

            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            Scene scene = new Scene(root, 900, 600);
            stage.setScene(scene);
            stage.show();
            System.out.println("NICEWORKING");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HOY NAAY ERROR ARI");
        }
    }
}
