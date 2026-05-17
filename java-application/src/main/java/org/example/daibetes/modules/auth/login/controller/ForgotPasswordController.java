package org.example.daibetes.modules.auth.login.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.example.daibetes.core.database.MySQLConnection;
import org.example.daibetes.shared.utils.PasswordUtils;
import org.example.daibetes.shared.ui.SceneLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;

    @FXML
    private void handleReset(ActionEvent event) {
        String email = emailField.getText().trim();
        String newPass = newPasswordField.getText();

        if (email.isEmpty() || newPass.isEmpty()) {
            statusLabel.setText("Please fill all fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try (Connection conn = MySQLConnection.getConnection()) {
            String hashedPass = PasswordUtils.hashPassword(newPass);
            String query = "UPDATE tbluser SET password = ? WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, hashedPass);
            pstmt.setString(2, email);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Password updated! Returning to Login.");
                alert.showAndWait();

                // Navigate back to login
                handleBack(event);
            } else {
                statusLabel.setText("Email not found.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Database error.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneLoader.switchScene(
                (Node) event.getSource(),
                "org/example/daibetes/modules/auth/login",
                "login-screen.fxml",
                "Log-in - dAIbetes",
                "/org/example/daibetes/styles/splash2.css"
        );
    }
}