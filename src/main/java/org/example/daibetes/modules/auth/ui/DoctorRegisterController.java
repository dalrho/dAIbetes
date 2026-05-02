package org.example.daibetes.modules.auth.ui;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.example.daibetes.shared.utils.ValidationUtils;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;

public class DoctorRegisterController {

    @FXML private TextField firstName;
    @FXML private TextField lastName;
    @FXML private TextField emailAddress;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField contactNumber;
    @FXML private ToggleGroup genderGroup;
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
                ValidationUtils.showAlert("Error", "Only PNG, JPG, or JPEG allowed.");
                return;
            }

            doctorId = selectedFile;
            uploadDoctorId.setText("Uploaded: " + doctorId.getName());
            ValidationUtils.showAlert("Success", "Doctor ID uploaded!");
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
        String selectedGender = "";
        if (genderGroup.getSelectedToggle() != null) {
            selectedGender = ((RadioButton) genderGroup.getSelectedToggle()).getText();
        }
        String hosp = hospital.getText().trim();
        String license = licenseNumber.getText().trim();
        LocalDate birth = birthDate.getValue();

        // validation
        if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty() ||
                contact.isEmpty() || selectedGender.isEmpty() ||
                hosp.isEmpty() || license.isEmpty() || birth == null) {

            ValidationUtils.showAlert("Error", "Please fill all fields.");
            return;
        }

        if (doctorId == null) {
            ValidationUtils.showAlert("Error", "Please upload your Doctor ID image.");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) return;

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
    }


    //clear form
    public void clearFields() {
        firstName.clear();
        lastName.clear();
        emailAddress.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        contactNumber.clear();
        genderGroup.selectToggle(null);
        birthDate.setValue(null);
        hospital.clear();
        licenseNumber.clear();
        doctorId = null;
    }
}