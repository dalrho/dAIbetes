package org.example.daibetes.modules.auth.login.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.modules.auth.viewmodel.LoginViewModel;
import org.example.daibetes.shared.ui.SceneLoader;

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
public class LoginController {

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
        // We use the switchScene method you already have in SceneLoader
        SceneLoader.switchScene(
                (Node) event.getSource(),                   // The node clicked
                "org/example/daibetes/modules/auth/register", // Folder
                "register-screen.fxml",                     // FXML file
                "Register - dAIbetes",                      // Window Title
                "/org/example/daibetes/styles/splash2.css"   // CSS
        );
    }

    // =========================================================================
    // Dashboard navigation — role resolved from DB, not from a toggle
    // =========================================================================

    private void navigateToDashboard() {
        // Store authenticated user in session
        AppContext.getInstance().setCurrentUser(viewModel.getAuthenticatedUser());

        if (viewModel.isDoctor()) {
            SceneLoader.switchScene(
                    loginButton,
                    "org/example/daibetes/modules/doctor/dashboard",
                    "doctor-dashboard.fxml",
                    "dAIbetes — Doctor Dashboard",
                    null
            );
        } else {
            SceneLoader.switchScene(
                    loginButton,
                    "org/example/daibetes/modules/patient/dashboard",
                    "patients-dashboard.fxml",
                    "dAIbetes — Patient Dashboard",
                    null
            );
        }
    }
}