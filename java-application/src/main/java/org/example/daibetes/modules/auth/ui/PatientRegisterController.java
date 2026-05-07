package org.example.daibetes.modules.auth.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.daibetes.core.database.CreateData;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.PatientFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import org.example.daibetes.modules.auth.service.Authenticate;
import org.example.daibetes.shared.utils.NavigationUtils;
import org.example.daibetes.shared.utils.ValidationUtils;

import java.time.LocalDate;
import java.time.Period;

public class PatientRegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private DatePicker birthdatePicker;

    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private RadioButton maleBtn;
    @FXML private RadioButton femaleBtn;

    @FXML private Button createAccountBtn;

    @FXML
    public void initialize() {
        ToggleGroup genderGroup = new ToggleGroup();
        maleBtn.setToggleGroup(genderGroup);
        femaleBtn.setToggleGroup(genderGroup);
    }

    @FXML
    private void handleCreateAccount() {
        register();
    }

    public void register() {
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String contact = contactField.getText().trim();
        LocalDate birth = birthdatePicker.getValue();

        String selectedGender = "";

        if (maleBtn.isSelected()) {
            selectedGender = "Male";
        } else if (femaleBtn.isSelected()) {
            selectedGender = "Female";
        }

        if (fullName.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                contact.isEmpty() || selectedGender.isEmpty() || birth == null) {

            ValidationUtils.showAlert("Error", "Please fill all fields.");
            return;
        }

        String[] nameParts = fullName.split("\\s+", 2);
        String fname = nameParts[0];
        String lname = nameParts.length > 1 ? nameParts[1] : "";

        if (lname.isEmpty()) {
            ValidationUtils.showAlert("Error", "Please enter both first name and last name.");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) return;

        Authenticate auth = new Authenticate();

        if (auth.emailExists(email)) {
            ValidationUtils.showAlert("Error", "Email already exists.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            ValidationUtils.showAlert("Error", "Passwords do not match.");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) return;

        if (birth.isAfter(LocalDate.now())) {
            ValidationUtils.showAlert("Error", "Birthdate cannot be in the future.");
            return;
        }

        int age = Period.between(birth, LocalDate.now()).getYears();

        UserFactory factory = new PatientFactory(
                fname, lname, email, password,
                contact, selectedGender, birth.toString(), age
        );

        User user = factory.createUser();
        Patient patient = (Patient) user;

        CreateData createData = new CreateData();
        boolean success = createData.createPatient(patient);

        if (success) {
            ValidationUtils.showAlert("Success", "Patient registered successfully!");
            clearFields();

            NavigationUtils.switchScene(
                    nameField,
                    "login",
                    "login-screen.fxml",
                    "dAIbetes — Login",
                    null
            );

        } else {
            ValidationUtils.showAlert("Error", "Registration failed.");
        }
    }

    @FXML
    private void goToLogin(MouseEvent event) {
        NavigationUtils.switchScene(
                (javafx.scene.Node) event.getSource(),
                "login",
                "login-screen.fxml",
                "dAIbetes — Login",
                null
        );
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        contactField.clear();
        birthdatePicker.setValue(null);

        passwordField.clear();
        confirmPasswordField.clear();

        maleBtn.setSelected(false);
        femaleBtn.setSelected(false);
    }
}