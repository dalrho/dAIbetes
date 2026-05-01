package org.example.daibetes.modules.auth.ui;

import org.example.daibetes.core.database.CreateData;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.PatientFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.Period;

public class PatientRegisterController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField emailAddress;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactNumber;
    @FXML private RadioButton gender;
    @FXML private DatePicker birthDate;

    public void register() {

        // 1. GET INPUT
        String fname = firstName.getText().trim();
        String lname = lastName.getText().trim();
        String email = emailAddress.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String contact = contactNumber.getText().trim();
        String selectedGender = gender.isSelected() ? gender.getText() : "";
        String bdate = birthDate.getValue() == null ? "" : birthDate.getValue().toString();

        // 2. VALIDATION
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                contact.isEmpty() || selectedGender.isEmpty() || bdate.isEmpty()) {

            showAlert("Error", "Please fill all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // 3. CREATE USER USING FACTORY (IMPORTANT)

        LocalDate birth = birthDate.getValue();
        if (birth == null) {
            showAlert("Error", "Please select birthdate.");
            return;
        }
        int age = Period.between(birth, LocalDate.now()).getYears();

        UserFactory factory = new PatientFactory(
                fname, lname, email, password,
                contact, selectedGender, bdate, age
        );

        User user = factory.createUser();
        Patient patient = (Patient) user;

        // 4. SAVE TO DATABASE
        CreateData createData = new CreateData();
        boolean success = createData.createPatient(patient);

        // 5. RESULT
        if (success) {
            showAlert("Success", "Patient registered successfully!");
            clearFields();
        } else {
            showAlert("Error", "Registration failed.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        firstName.clear();
        lastName.clear();
        emailAddress.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        contactNumber.clear();
        gender.setSelected(false);
        birthDate.setValue(null);
    }
}