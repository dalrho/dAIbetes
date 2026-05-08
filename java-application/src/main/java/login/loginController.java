package login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.daibetes.app.AppContext;
import org.example.daibetes.modules.auth.viewmodel.LoginViewModel;
import register.sceneLoader;

/**
 * Controller for login-screen.fxml.
 * Package: login (matches fx:controller in FXML).
 *
 * Navigation uses sceneLoader exclusively — NavigationUtils is not used.
 * Authenticated user is stored in AppContext before switching scenes.
 */
public class loginController {

    @FXML private TextField     emailtxtfield;
    @FXML private PasswordField passwordField;
    @FXML private ToggleButton  doctorToggle;
    @FXML private ToggleButton  patientToggle;
    @FXML private Button        loginButton;
    @FXML private Label         errorLabel;
    @FXML private Label         registerLabel;

    private final LoginViewModel viewModel = new LoginViewModel();

    @FXML
    public void initialize() {
        // Bind inputs → ViewModel
        emailtxtfield.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        // Bind outputs ← ViewModel
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        loginButton.disableProperty().bind(viewModel.isLoadingProperty());

        // Toggle group — only one role active at a time
        ToggleGroup roleGroup = new ToggleGroup();
        doctorToggle.setToggleGroup(roleGroup);
        patientToggle.setToggleGroup(roleGroup);

        // Navigate after successful login
        viewModel.loginSuccessProperty().addListener((obs, wasSuccess, isSuccess) -> {
            if (isSuccess) Platform.runLater(this::navigateToDashboard);
        });
    }

    // =========================================================================
    // Role toggle
    // =========================================================================

    @FXML
    private void onRoleToggled() {
        if (doctorToggle.isSelected()) {
            viewModel.roleProperty().set(LoginViewModel.Role.DOCTOR);
            doctorToggle.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 0; " +
                            "-fx-border-color: white; -fx-text-fill: black;");
            patientToggle.setStyle(
                    "-fx-background-color: transparent; -fx-background-radius: 0; " +
                            "-fx-border-color: white; -fx-text-fill: #ffffff99;");

        } else if (patientToggle.isSelected()) {
            viewModel.roleProperty().set(LoginViewModel.Role.PATIENT);
            patientToggle.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 0; " +
                            "-fx-border-color: white; -fx-text-fill: black;");
            doctorToggle.setStyle(
                    "-fx-background-color: transparent; -fx-background-radius: 0; " +
                            "-fx-border-color: white; -fx-text-fill: white;");

        } else {
            viewModel.roleProperty().set(LoginViewModel.Role.NONE);
        }
    }

    // =========================================================================
    // Login
    // =========================================================================

    @FXML
    private void onLoginButtonClicked() {
        viewModel.login();
    }

    // =========================================================================
    // Register navigation
    // =========================================================================

    @FXML
    private void onRegisterClicked(MouseEvent event) {
        sceneLoader.switchScene(
                registerLabel,
                "register",
                "register-screen.fxml",
                "dAIbetes — Register",
                null
        );
    }

    // =========================================================================
    // Dashboard navigation
    // =========================================================================

    private void navigateToDashboard() {
        // Store authenticated user in session before switching screens
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