package register;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

public class registerController {

    @FXML
    public void selectPatient(ActionEvent event) {
        openScene(event, "register", "patient-register.fxml", "/styles/splash.css");
    }

    @FXML
    public void selectDoctor(ActionEvent event) {
        openScene(event, "register", "doctor-register.fxml", "/styles/splash.css");
    }

    @FXML
    public void goToLogin(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load("login", "login-screen.fxml", "/styles/splash.css")
        );
    }

    private void openScene(ActionEvent event, String folder, String fxml, String css) {
        try {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(sceneLoader.load(folder, fxml, css));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}