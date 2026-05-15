package login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.modules.auth.viewmodel.LoginViewModel;
import register.sceneLoader;

/**
 * Controller for login-screen.fxml.
 * Package: login (matches fx:controller="login.loginController").
 *
 * fx:id mapping:
 *   nameField     → email input   (TextField)
 *   passwordField → password input (PasswordField)
 *   loginButton   → triggers login (Button, onAction="#handleLogin")
 *   errorLabel    → error display  (Label)
 *
 * No role toggle — role is resolved automatically from the DB after login.
 * Navigation uses sceneLoader exclusively.
 */
public class loginController {

    @FXML private TextField     nameField;
    @FXML private PasswordField passwordField;
    @FXML private Button        loginButton;
    @FXML private Label         errorLabel;

    private final LoginViewModel viewModel = new LoginViewModel();

    // =========================================================================
    // Initialize — bindings only, no premature login() call
    // =========================================================================

    @FXML
    public void initialize() {
        // Bind inputs → ViewModel
        nameField.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        // Bind outputs ← ViewModel
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        loginButton.disableProperty().bind(viewModel.isLoadingProperty());

        // Navigate after successful login
        viewModel.loginSuccessProperty().addListener((obs, wasSuccess, isSuccess) -> {
            if (isSuccess) Platform.runLater(this::navigateToDashboard);
        });
    }

    // =========================================================================
    // Login
    // =========================================================================

    @FXML
    private void handleLogin() {
        viewModel.login();
    }

    // =========================================================================
    // Register navigation
    // =========================================================================

    @FXML
    private void goToRegister(MouseEvent event) {
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

    // =========================================================================
    // Dashboard navigation — role resolved from DB, not from a toggle
    // =========================================================================

    private void navigateToDashboard() {
        // Store authenticated user in session
        AppContext.getInstance().setCurrentUser(viewModel.getAuthenticatedUser());

        if (viewModel.isDoctor()) {
            sceneLoader.switchScene(
                    loginButton,
                    "doctorDashboard",
                    "doctor-dashboard.fxml",
                    "dAIbetes — Doctor Dashboard",
                    null
            );
        } else {
            sceneLoader.switchScene(
                    loginButton,
                    "patientsdashboard",
                    "patients-dashboard.fxml",
                    "dAIbetes — Patient Dashboard",
                    null
            );
        }
    }
}