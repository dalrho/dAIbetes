package org.example.daibetes.modules.auth.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.example.daibetes.core.database.CreateData;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.DoctorFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import org.example.daibetes.modules.auth.service.Authenticate;
import org.example.daibetes.shared.utils.NavigationUtils;
import org.example.daibetes.shared.utils.ValidationUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;

public class DoctorRegisterController {

    @FXML private TextField doctorIdField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField contactField;
    @FXML private DatePicker birthdatePicker;

    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private RadioButton maleBtn;
    @FXML private RadioButton femaleBtn;

    @FXML private Button createAccountBtn;

    private File doctorIdImage;

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
        String doctorIdOrLicense = doctorIdField.getText().trim();
        String fullName = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        LocalDate birth = birthdatePicker.getValue();

        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String selectedGender = "";

        if (maleBtn.isSelected()) {
            selectedGender = "Male";
        } else if (femaleBtn.isSelected()) {
            selectedGender = "Female";
        }

        if (doctorIdOrLicense.isEmpty() || fullName.isEmpty() || email.isEmpty() ||
                contact.isEmpty() || birth == null ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                selectedGender.isEmpty()) {

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

        if (age < 18) {
            ValidationUtils.showAlert("Error", "Doctor must be at least 18 years old.");
            return;
        }

        /*
         The doctor-register.fxml only has doctorIdField. It does not have a hospital field or image upload button.

         * So for now:
         * - doctorIdOrLicense is used as the license number
         * - hospital is set as "Not specified"
         * - doctorIdImage is null
         */

        String license = doctorIdOrLicense; //not implemented in the ui yet, ignore
        String hospital = "Not specified"; //not implemented in the ui yet, ignore

        UserFactory factory = new DoctorFactory(
                fname, lname, email, password,
                contact, selectedGender, birth.toString(),
                license, hospital, doctorIdImage
        );

        User user = factory.createUser();
        Doctor doctor = (Doctor) user;

        CreateData createData = new CreateData();
        boolean success = createData.createDoctor(doctor);

        if (success) {
            ValidationUtils.showAlert("Success", "Doctor registered successfully!");
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
        doctorIdField.clear();
        nameField.clear();
        emailField.clear();
        contactField.clear();
        birthdatePicker.setValue(null);

        passwordField.clear();
        confirmPasswordField.clear();

        maleBtn.setSelected(false);
        femaleBtn.setSelected(false);

        doctorIdImage = null;
    }
}