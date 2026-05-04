package login;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import register.sceneLoader;

public class loginController {

    @FXML private TextField nameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        if (nameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
        } else {
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(
                    sceneLoader.load(
                            "doctorDashboard",
                            "doctor-dashboard.fxml",
                            "/styles/splash.css"
                    )
            );
            showAlert("Success", "Login attempted.");
        }
    }

    @FXML
    public void goToRegister(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load(
                        "register",
                        "register-screen.fxml",
                        "/styles/splash.css"
                )
        );
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}