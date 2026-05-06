package org.example.daibetes.shared.utils;

import javafx.scene.control.Alert;

public class ValidationUtils {

    public static boolean isValidPassword(String password) {

        if (password == null || password.length() < 8) {
            showAlert("Error", "Password must be at least 8 characters long.");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            showAlert("Error", "Password must contain at least one uppercase letter.");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            showAlert("Error", "Password must contain at least one lowercase letter.");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            showAlert("Error", "Password must contain at least one number.");
            return false;
        }

        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            showAlert("Error", "Password must contain at least one special character.");
            return false;
        }

        return true;
    }

    public static boolean isValidEmail(String email) {

        if (email == null || email.isBlank()) {
            showAlert("Error", "Email is required.");
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        if (!email.matches(regex)) {
            showAlert("Error", "Enter a valid email address.");
            return false;
        }

        return true;
    }

    //message alert
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
