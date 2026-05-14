package org.example.daibetes.modules.auth.viewmodel;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import org.example.daibetes.core.database.RetrieveData;
import org.example.daibetes.core.domain.Doctor;
import org.example.daibetes.core.domain.Patient;
import org.example.daibetes.core.domain.User;
import org.example.daibetes.modules.auth.service.Authenticate;
import register.sceneLoader;

/**
 * ViewModel for login-screen.fxml (MVVM).
 * Package: org.example.daibetes.modules.auth.viewmodel
 *
 * Auth flow:
 *   1. Authenticate.login()       → verifies credentials exist in tblUser
 *   2. RetrieveData.getUserByEmail() → fetches typed Doctor or Patient object
 *   3. Role cross-check           → confirms DB role matches the toggle selection
 */
public class LoginViewModel {

    public enum Role { DOCTOR, PATIENT, NONE }

    // --- Bindable input properties ---
    private final StringProperty       email    = new SimpleStringProperty("");
    private final StringProperty       password = new SimpleStringProperty("");
    private final ObjectProperty<Role> role     = new SimpleObjectProperty<>(Role.NONE);

    // --- Bindable output properties ---
    private final StringProperty  errorMessage = new SimpleStringProperty("");
    private final BooleanProperty isLoading    = new SimpleBooleanProperty(false);
    private final BooleanProperty loginSuccess = new SimpleBooleanProperty(false);

    private User authenticatedUser;

    private final Authenticate  authenticate = new Authenticate();
    private final RetrieveData  retrieveData = new RetrieveData();

    // =========================================================================
    // Login action
    // =========================================================================

    public void login() {
        errorMessage.set("");
        loginSuccess.set(false);

        String emailVal    = email.get().trim();
        String passwordVal = password.get().trim();

        // Client-side validation
        if (emailVal.isEmpty() || passwordVal.isEmpty()) {
            errorMessage.set("Email and password are required.");
            return;
        }
        if (!emailVal.contains("@")) {
            errorMessage.set("Please enter a valid email address.");
            return;
        }
        if (role.get() == Role.NONE) {
            errorMessage.set("Please select Doctor or Patient.");
            return;
        }

        isLoading.set(true);

        // Step 1: verify credentials
        boolean credentialsValid = authenticate.login(emailVal, passwordVal);

        if (!credentialsValid) {
            isLoading.set(false);
            errorMessage.set("Invalid email or password.");
            return;
        }

        // Step 2: fetch typed User object
        User user = retrieveData.getUserByEmail(emailVal);

        isLoading.set(false);

        if (user == null) {
            errorMessage.set("Account not found. Please contact support.");
            return;
        }

        // Step 3: cross-check DB role against toggle selection
        if (role.get() == Role.DOCTOR && !(user instanceof Doctor)) {
            errorMessage.set("No doctor account found with these credentials.");
            return;
        }
        if (role.get() == Role.PATIENT && !(user instanceof Patient)) {
            errorMessage.set("No patient account found with these credentials.");
            return;
        }

        authenticatedUser = user;
        loginSuccess.set(true);
    }

    // =========================================================================
    // Role helpers
    // =========================================================================

    public boolean isDoctor()  { return authenticatedUser instanceof Doctor; }
    public boolean isPatient() { return authenticatedUser instanceof Patient; }

    public Doctor  getAuthenticatedDoctor()  { return (Doctor)  authenticatedUser; }
    public Patient getAuthenticatedPatient() { return (Patient) authenticatedUser; }
    public User    getAuthenticatedUser()    { return authenticatedUser; }

    // =========================================================================
    // Property accessors
    // =========================================================================

    public StringProperty       emailProperty()        { return email; }
    public StringProperty       passwordProperty()     { return password; }
    public ObjectProperty<Role> roleProperty()         { return role; }
    public StringProperty       errorMessageProperty() { return errorMessage; }
    public BooleanProperty      isLoadingProperty()    { return isLoading; }
    public BooleanProperty      loginSuccessProperty() { return loginSuccess; }

    public void navigateToDashboard(Stage stage){
        stage.setScene(
                sceneLoader.load(
                        "doctorDashboard",
                        "doctor-dashboard.fxml",
                        "/styles/splash.css"
                )
        );
        stage.setTitle("dAIbetes - Login");
        stage.show();

    }
}