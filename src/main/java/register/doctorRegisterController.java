package register;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class doctorRegisterController {

    @FXML private VBox card;

    @FXML private TextField doctorIdField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;

    @FXML private DatePicker birthdatePicker;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private RadioButton maleBtn;
    @FXML private RadioButton femaleBtn;
    @FXML private HBox genderBox;

    @FXML private Button createAccountBtn;

    @FXML
    public void initialize() {
        // Gender toggle group
        ToggleGroup genderGroup = new ToggleGroup();
        maleBtn.setToggleGroup(genderGroup);
        femaleBtn.setToggleGroup(genderGroup);

        // Prevent radio button labels from being clipped — must be set in code, not FXML
        maleBtn.setMinWidth(Region.USE_PREF_SIZE);
        femaleBtn.setMinWidth(Region.USE_PREF_SIZE);

        // Entrance animation
        card.setOpacity(0);
        card.setScaleX(0.85);
        card.setScaleY(0.85);

        FadeTransition fade = new FadeTransition(Duration.millis(300), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), card);
        scale.setFromX(0.85);
        scale.setFromY(0.85);
        scale.setToX(1.0);
        scale.setToY(1.0);

        new ParallelTransition(fade, scale).play();
    }

    @FXML
    public void handleCreateAccount(ActionEvent event) {
        if (doctorIdField.getText().isBlank() ||
                nameField.getText().isBlank() ||
                emailField.getText().isBlank() ||
                contactField.getText().isBlank() ||
                birthdatePicker.getValue() == null ||
                passwordField.getText().isBlank() ||
                confirmPasswordField.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Fields", "Please fill in all fields.");
            return;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match.");
            return;
        }
        if (maleBtn.getToggleGroup().getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Gender Required", "Please select a gender.");
            return;
        }
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
        // TODO: persist to database / service layer
        showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor account created successfully!");
        closeWindow(event);
    }

    public void goToLogin(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load("login", "login-screen.fxml", "/styles/splash.css")
        );
    }

    private void closeWindow(Object eventSource) {
        Node source = null;
        if (eventSource instanceof ActionEvent ae && ae.getSource() instanceof Node n) source = n;
        else if (eventSource instanceof MouseEvent me && me.getSource() instanceof Node n) source = n;
        if (source != null) ((Stage) source.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
