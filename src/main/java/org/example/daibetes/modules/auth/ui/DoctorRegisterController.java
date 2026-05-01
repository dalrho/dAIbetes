package org.example.daibetes.modules.auth.ui;

import org.example.daibetes.core.database.CreateData;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.DoctorFactory;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.core.domain.UserFactory;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

public class DoctorRegisterController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField emailAddress;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactNumber;
    @FXML private RadioButton gender;
    @FXML private DatePicker birthDate;

    @FXML private TextField hospital;
    @FXML private TextField licenseNumber;
    @FXML private Button uploadDoctorId;

    private File doctorId; // selected image

    // upload doctor's id
    public void uploadDoctorId() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Doctor ID");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(uploadDoctorId.getScene().getWindow());

        if (selectedFile != null) {
            String name = selectedFile.getName().toLowerCase();

            // STRICT validation
            if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))) {
                showAlert("Error", "Only PNG, JPG, or JPEG allowed.");
                return;
            }

            doctorId = selectedFile;
            showAlert("Success", "Doctor ID uploaded!");
        }
    }

    //registration for doctors
    public void register() {

        String fname = firstName.getText().trim();
        String lname = lastName.getText().trim();
        String email = emailAddress.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String contact = contactNumber.getText().trim();
        String selectedGender = gender.isSelected() ? gender.getText() : "";
        String hosp = hospital.getText().trim();
        String license = licenseNumber.getText().trim();
        LocalDate birth = birthDate.getValue();

        // validation
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                contact.isEmpty() || selectedGender.isEmpty() ||
               hosp.isEmpty() || license.isEmpty() || doctorId == null || birth == null) {

            showAlert("Error", "Please fill all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        // age validation, doctor must be 18 years old and above
        int age = java.time.Period.between(birth, LocalDate.now()).getYears();
        if (age < 18) {
            showAlert("Error", "Doctor must be at least 18 years old.");
            return;
        }

        // create obj using factory
        UserFactory factory = new DoctorFactory(
                fname, lname, email, password,
                contact, selectedGender, birth.toString(),
                license, hosp, doctorId
        );

        User user = factory.createUser();
        Doctor doctor = (Doctor) user;

        //save to database
        CreateData createData = new CreateData();
        boolean success = createData.createDoctor(doctor);

        if (success) {
            showAlert("Success", "Doctor registered successfully!");
            clearFields();
        } else {
            showAlert("Error", "Registration failed.");
        }
    }

    //message alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //clear form
    private void clearFields() {
        firstName.clear();
        lastName.clear();
        emailAddress.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        contactNumber.clear();
        gender.setSelected(false);
        birthDate.setValue(null);
        hospital.clear();
        licenseNumber.clear();
        doctorId = null;
    }
}