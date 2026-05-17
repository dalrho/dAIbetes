package org.example.daibetes.modules.auth.login.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.daibetes.core.database.MySQLConnection;
import org.example.daibetes.shared.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleReset() {
        String email = emailField.getText().trim();
        String newPass = newPasswordField.getText();

        if (email.isEmpty() || newPass.isEmpty()) {
            statusLabel.setText("Please fill all fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            // Hash the new password using your utility
            String hashedPass = PasswordUtils.hashPassword(newPass);

            String query = "UPDATE tbluser SET password = ? WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, hashedPass);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // 1. Show success alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Reset");
                alert.setHeaderText(null);
                alert.setContentText("Your password has been updated successfully!");
                alert.showAndWait(); // Execution pauses here until user clicks OK

                // 2. Close the popup
                closeWindow();
            } else {
                statusLabel.setText("Email not found in our records.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Database error: Could not update password.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }
}