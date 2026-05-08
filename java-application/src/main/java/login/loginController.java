package login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
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