package org.example.daibetes.modules.auth.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.DoctorFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import org.example.daibetes.modules.auth.service.Authenticate;
import org.example.daibetes.shared.utils.PasswordUtils;
import org.example.daibetes.shared.utils.ValidationUtils;
import register.sceneLoader;

import java.time.LocalDate;
import java.time.Period;

public class DoctorRegisterController {
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

        String doctorIdOrLicense = doctorIdField.getText().trim();
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String password = passwordField.getText();
        String hashedPassword = PasswordUtils.hashPassword(password);
        LocalDate birthdate = birthdatePicker.getValue();

        String selectedGender = ((RadioButton) maleBtn.getToggleGroup()
                .getSelectedToggle())
                .getText();

        String[] nameParts = fullName.split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        if (lastName.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Name", "Please enter both first name and last name.");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            return;
        }

        if (birthdate.isAfter(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Birthdate", "Birthdate cannot be in the future.");
            return;
        }

        int age = Period.between(birthdate, LocalDate.now()).getYears();

        if (age < 18) {
            showAlert(Alert.AlertType.ERROR, "Invalid Age", "Doctor must be at least 18 years old.");
            return;
        }

        Authenticate auth = new Authenticate();

        if (auth.emailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Email Exists", "Email is already registered.");
            return;
        }

        String licenseNumber = doctorIdField.getText().trim();
        String hospital = "Not specified";

        UserFactory factory = new DoctorFactory(
                firstName,
                lastName,
                email,
                hashedPassword,
                contact,
                selectedGender,
                birthdate.toString(),
                licenseNumber,
                hospital,
                null
        );

        User user = factory.createUser();
        Doctor doctor = (Doctor) user;

        CreateData createData = new CreateData();
        boolean success = createData.createDoctor(doctor);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor account created successfully!");

            goToLogin(event);

        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Doctor account was not saved.");
        }
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

    @FXML
    private void goToLogin(Event event) {
        Stage stage = (Stage) ((Node) event.getSource())
                .getScene()
                .getWindow();

        stage.setScene(
                sceneLoader.load("login", "login-screen.fxml", "/styles/splash.css")
        );

        stage.setTitle("dAIbetes — Login");
        stage.show();
    }
}