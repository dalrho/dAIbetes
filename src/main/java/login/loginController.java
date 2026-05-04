package login;

import doctorDashboard.doctorDashboardController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;
import patientsdashboard.patientsdashboardController;

import java.io.IOException;

/**
 * Controller for login-screen.fxml.
 * Package: login (matches fx:controller="login.loginController").
 *
 * fx:id mapping:
 *   emailtxtfield  → email input    (TextField)
 *   passwordField  → password input (PasswordField)
 *   doctorToggle   → Doctor button  (ToggleButton)
 *   patientToggle  → Patient button (ToggleButton)
 *   loginButton    → Sign In button (Button)
 *   errorLabel     → error display  (Label)
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
        // Wire inputs → ViewModel
        emailtxtfield.textProperty().bindBidirectional(viewModel.emailProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());

        // Wire outputs ← ViewModel
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
        loginButton.disableProperty().bind(viewModel.isLoadingProperty());

        // Toggle group: only one role active at a time
        ToggleGroup roleGroup = new ToggleGroup();
        doctorToggle.setToggleGroup(roleGroup);
        patientToggle.setToggleGroup(roleGroup);

        // Listen for navigation trigger
        viewModel.loginSuccessProperty().addListener((obs, wasSuccess, isSuccess) -> {
            if (isSuccess) Platform.runLater(this::navigateToDashboard);
        });
    }

    @FXML
    private void onRoleToggled() {
        if (doctorToggle.isSelected()) {
            viewModel.roleProperty().set(LoginViewModel.Role.DOCTOR);
            // Visual feedback: highlight selected toggle
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
            // Deselected both — reset
            viewModel.roleProperty().set(LoginViewModel.Role.NONE);
        }
    }

    @FXML
    private void onLoginButtonClicked() {
        viewModel.login();
    }

    @FXML
    private void onRegisterClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/register/register-screen.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) registerLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load registration page.");
        }
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    private void navigateToDashboard() {
        try {
            String fxmlPath;
            FXMLLoader loader;
            Parent root;

            if (viewModel.isDoctor()) {
                Doctor doctor = viewModel.getAuthenticatedDoctor();
                fxmlPath = "/org/example/daibetes/ui/view/DoctorDashboard.fxml";
                loader   = new FXMLLoader(getClass().getResource(fxmlPath));
                root     = loader.load();

                doctorDashboardController ctrl = loader.getController();
                ctrl.initData(doctor);

            } else {
                Patient patient = viewModel.getAuthenticatedPatient();
                fxmlPath = "/org/example/daibetes/ui/view/PatientDashboard.fxml";
                loader   = new FXMLLoader(getClass().getResource(fxmlPath));
                root     = loader.load();

                patientsdashboardController ctrl = loader.getController();
                ctrl.initData(patient);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load dashboard. Please try again.");
        }
    }
}