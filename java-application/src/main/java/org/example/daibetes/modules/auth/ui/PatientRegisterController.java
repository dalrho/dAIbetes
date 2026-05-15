package org.example.daibetes.modules.auth.ui;

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
import org.example.daibetes.core.database.CreateData;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.PatientFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import org.example.daibetes.modules.auth.service.Authenticate;
import register.sceneLoader;

import java.time.LocalDate;
import java.time.Period;

public class PatientRegisterController {

    @FXML private VBox card;

    @FXML private TextField     nameField;
    @FXML private TextField     emailField;
    @FXML private TextField     contactField;
    @FXML private DatePicker    birthdatePicker;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private RadioButton   maleBtn;
    @FXML private RadioButton   femaleBtn;
    @FXML private HBox          genderBox;
    @FXML private Button        createAccountBtn;

    @FXML
    public void initialize() {
        ToggleGroup genderGroup = new ToggleGroup();
        maleBtn.setToggleGroup(genderGroup);
        femaleBtn.setToggleGroup(genderGroup);

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

        // ── Field validation ──────────────────────────────────────────────────
        String fullName         = nameField.getText().trim();
        String email            = emailField.getText().trim();
        String contact          = contactField.getText().trim();
        LocalDate birth         = birthdatePicker.getValue();
        String password         = passwordField.getText();
        String confirmPassword  = confirmPasswordField.getText();

        if (fullName.isBlank() || email.isBlank() || contact.isBlank() ||
                birth == null || password.isBlank() || confirmPassword.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Fields",
                    "Please fill in all fields.");
            return;
        }

        if (maleBtn.getToggleGroup().getSelectedToggle() == null) {
            showAlert(Alert.AlertType.WARNING, "Gender Required",
                    "Please select a gender.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch",
                    "Passwords do not match.");
            return;
        }

        // ── Name split ────────────────────────────────────────────────────────
        String[] nameParts = fullName.split("\\s+", 2);
        String fname = nameParts[0];
        String lname = nameParts.length > 1 ? nameParts[1] : "";

        if (lname.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Full Name Required",
                    "Please enter both first and last name.");
            return;
        }

        // ── Email duplicate check ─────────────────────────────────────────────
        Authenticate auth = new Authenticate();
        if (auth.emailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Email Taken",
                    "An account with this email already exists.");
            return;
        }

        // ── Age ───────────────────────────────────────────────────────────────
        if (birth.isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Birthdate",
                    "Birthdate cannot be in the future.");
            return;
        }

        int age = Period.between(birth, LocalDate.now()).getYears();

        String selectedGender = ((RadioButton) maleBtn.getToggleGroup()
                .getSelectedToggle()).getText();

        // ── Persist to DB via Factory → CreateData ────────────────────────────
        UserFactory factory = new PatientFactory(
                fname, lname, email, password,
                contact, selectedGender, birth.toString(), age
        );

        User user     = factory.createUser();
        Patient patient = (Patient) user;

        CreateData createData = new CreateData();
        boolean success = createData.createPatient(patient);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Patient account created successfully!");

            // Navigate to login after registration
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(sceneLoader.load("login", "login-screen.fxml",
                    "/styles/splash.css"));
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed",
                    "Could not create account. Please try again.");
        }
    }

    @FXML
    public void goToLogin(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(sceneLoader.load("login", "login-screen.fxml",
                "/styles/splash.css"));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}