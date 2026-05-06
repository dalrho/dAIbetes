package org.example.daibetes.modules.auth.ui;

import org.example.daibetes.core.database.CreateData;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.PatientFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.daibetes.modules.auth.service.Authenticate;
import org.example.daibetes.shared.utils.ValidationUtils;
import java.time.LocalDate;
import java.time.Period;
public class PatientRegisterController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField emailAddress;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactNumber;
    @FXML private ToggleGroup genderGroup;
    @FXML private DatePicker birthDate;

    public void register() {

        String fname = firstName.getText().trim();
        String lname = lastName.getText().trim();
        String email = emailAddress.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String contact = contactNumber.getText().trim();

        String selectedGender = "";
        if (genderGroup.getSelectedToggle() != null) {
            selectedGender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
        }

        LocalDate birth = birthDate.getValue();

        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                contact.isEmpty() || selectedGender.isEmpty() || birth == null) {

            ValidationUtils.showAlert("Error", "Please fill all fields.");
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
        } else {
            ValidationUtils.showAlert("Error", "Registration failed.");
        }
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        emailAddress.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        contactNumber.clear();
        genderGroup.selectToggle(null);
        birthDate.setValue(null);
    }
}